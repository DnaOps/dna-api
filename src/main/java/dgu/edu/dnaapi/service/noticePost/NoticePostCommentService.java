package dgu.edu.dnaapi.service.noticePost;

import dgu.edu.dnaapi.controller.dto.CommentSearchCondition;
import dgu.edu.dnaapi.domain.*;
import dgu.edu.dnaapi.domain.dto.comment.CommentSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.comment.CommentUpdateRequestDto;
import dgu.edu.dnaapi.domain.dto.noticePostComment.NoticePostCommentResponseDto;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.domain.response.ListResponse;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.noticePost.NoticePostCommentLikeRepository;
import dgu.edu.dnaapi.repository.noticePost.NoticePostCommentRepository;
import dgu.edu.dnaapi.repository.noticePost.NoticePostRepository;
import dgu.edu.dnaapi.service.vo.PostCommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NoticePostCommentService {

    private final NoticePostRepository noticePostRepository;
    private final NoticePostCommentRepository noticePostCommentRepository;
    private final NoticePostCommentLikeRepository noticePostCommentLikeRepository;

    @Transactional
    public Long save(User user, CommentSaveRequestDto requestDto, Long noticePostId) {
        NoticePostComment comment = requestDto.toNoticeComment(user);
        NoticePost noticePost = noticePostRepository.findById(noticePostId).orElseThrow(
                () -> new DNACustomException("해당 글이 없습니다. id=" + noticePostId, DnaStatusCode.INVALID_POST));
        comment.registerNoticePost(noticePost);

        if(requestDto.getParentCommentId() != null) {
            Long parentCommentId = requestDto.getParentCommentId();
            NoticePostComment parent = noticePostCommentRepository.findWithNoticePostByNoticePostCommentId(parentCommentId).orElseThrow(
                    () -> new DNACustomException("해당 댓글이 없습니다. id=" + parentCommentId, DnaStatusCode.INVALID_COMMENT));
            if(parent.getNoticePost().getNoticePostId() != noticePostId)
                throw new DNACustomException("대댓글 부모 연관관계가 잘못되었습니다.", DnaStatusCode.INVALID_COMMENT);
            comment.registerParentNoticeComment(parent);
        }
        noticePostRepository.increaseCommentCount(noticePostId);
        return noticePostCommentRepository.save(comment).getNoticePostCommentId();
    }

    @Transactional
    public Long update(Long userId, CommentUpdateRequestDto requestDto, Long noticePostCommentId, Long noticePostId) {
        NoticePostComment noticePostComment = noticePostCommentRepository.findWithNoticePostByNoticePostCommentId(noticePostCommentId).orElseThrow(
                () -> new DNACustomException("해당 댓글이 없습니다. id=" + noticePostCommentId, DnaStatusCode.INVALID_COMMENT));
        if(!noticePostId.equals(noticePostComment.getNoticePost().getNoticePostId())){
            throw new DNACustomException("해당 댓글이 해당 게시글에 존재하지 않습니다.", DnaStatusCode.INVALID_COMMENT);
        }
        if(!noticePostComment.getAuthor().getId().equals(userId)) {
            throw new DNACustomException("작성자만 댓글을 수정할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        noticePostComment.update(requestDto.getContent());
        return noticePostComment.getNoticePostCommentId();
    }

    @Transactional
    public Long delete(Long userId, Long deleteNoticePostCommentId, Long noticePostId) {
        NoticePostComment noticePostComment = noticePostCommentRepository.findWithAuthorAndNoticePostByNoticePostCommentId(deleteNoticePostCommentId).orElseThrow(
                () -> new DNACustomException("해당 댓글이 없습니다. id=" + deleteNoticePostCommentId, DnaStatusCode.INVALID_COMMENT));
        if(noticePostComment.isDeleted())
            throw new DNACustomException("이미 삭제된 댓글입니다. id = " + deleteNoticePostCommentId ,DnaStatusCode.INVALID_DELETE_COMMENT);
        if(!noticePostComment.getAuthor().getId().equals(userId)) {
            throw new DNACustomException("작성자만 댓글을 삭제할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        if (!noticePostId.equals(noticePostComment.getNoticePost().getNoticePostId())) {
            throw new DNACustomException("해당 댓글이 해당 게시글에 존재하지 않습니다.", DnaStatusCode.INVALID_COMMENT);
        }

        noticePostRepository.decreaseCommentCount(noticePostComment.getNoticePost().getNoticePostId());
        Long commentGroupId = noticePostComment.getCommentGroupId();
        List<NoticePostComment> noticePostCommentList = new ArrayList<>();
        if(commentGroupId == null){
            commentGroupId = noticePostComment.getNoticePostCommentId();
            noticePostCommentList.add(noticePostComment);
        }
        List<NoticePostComment> allReplyComments = noticePostCommentRepository.findAllReplyNoticePostCommentsByCommentGroupId(noticePostComment.getNoticePost().getNoticePostId(), commentGroupId);
        noticePostCommentList.addAll(allReplyComments);
        List<PostCommentVO> postCommentVOList = noticePostCommentList.stream().map(PostCommentVO::convertToPostCommentVO).collect(Collectors.toList());

        Map<Long, PostCommentVO> noticePostCommentsMap = createNoticePostCommentHierarchyStructureMap(postCommentVOList);

        PostCommentVO deletedPostCommentVO = noticePostCommentsMap.get(deleteNoticePostCommentId);
        deletedPostCommentVO.setDeletedStatus();
        if (checkAllMyChildNoticePostCommentIsDeleted(deletedPostCommentVO.getChildrenComment())){
            noticePostCommentRepository.deleteById(deleteNoticePostCommentId);
        }else{
            noticePostCommentRepository.softDelete(deleteNoticePostCommentId);
        }
        noticePostCommentLikeRepository.deleteAllNoticePostCommentLikesByNoticePostCommentId(deleteNoticePostCommentId);
        if(deletedPostCommentVO.hasParentComment())
            shouldParentCommentBeDeleted(noticePostCommentsMap, deletedPostCommentVO.getParentCommentId());

        return deleteNoticePostCommentId;
    }

    public ListResponse findAllNoticePostCommentsByCommentSearchCondition(CommentSearchCondition commentSearchCondition, Long noticePostId, Pageable pageable) {
        List<NoticePostComment> noticePostCommentList = noticePostCommentRepository.search(noticePostId, commentSearchCondition.getStart(), pageable);
        boolean hasNext = isHasNext(pageable.getPageSize(), noticePostCommentList);

        List<Long> commentIds = getNoticePostCommentIds(noticePostCommentList);
        noticePostCommentList.addAll(noticePostCommentRepository.findAllReplyNoticePostComments(noticePostId, commentIds));
        List<NoticePostCommentResponseDto> noticePostCommentResponseDtoList = convertToNoticePostCommentResponseDto(noticePostCommentList);
        Set<Long> noticeCommentIdsLikedByUser = new HashSet<>();
        if(commentSearchCondition.hasUserId()){
            noticeCommentIdsLikedByUser = getNoticePostCommentIdsLikedByUser(commentSearchCondition.getUserId(), getNoticePostCommentIds(noticePostCommentList));
        }
        List<NoticePostCommentResponseDto> result = createNoticePostCommentResponseHierarchyStructure(noticePostCommentResponseDtoList, noticeCommentIdsLikedByUser);
        return ListResponse.builder()
                    .list(result)
                    .hasNext(hasNext)
                    .build();
    }

    private boolean isHasNext(int pageSize, List<NoticePostComment> commentsList) {
        if(commentsList.size() > pageSize){
            commentsList.remove(pageSize);
            return true;
        }
        return false;
    }

    private Set<Long> getNoticePostCommentIdsLikedByUser(Long userId, List<Long> allNoticeCommentIds) {
        return noticePostCommentLikeRepository.findNoticePostCommentIdsLikedByUserId(userId, allNoticeCommentIds);
    }

    private List<NoticePostCommentResponseDto> convertToNoticePostCommentResponseDto(List<NoticePostComment> commentsList) {
        return commentsList.stream().
                map(NoticePostCommentResponseDto::convertNoticeCommentToResponseDto).collect(Collectors.toList());
    }

    private List<Long> getNoticePostCommentIds(List<NoticePostComment> commentsList) {
        return commentsList.stream().map(NoticePostComment::getNoticePostCommentId).collect(Collectors.toList());
    }

    private List<NoticePostCommentResponseDto> createNoticePostCommentResponseHierarchyStructure(List<NoticePostCommentResponseDto> noticePostCommentResponseDtos, Set<Long> noticePostCommentIdsLikedByUser){
        List<NoticePostCommentResponseDto> result = new ArrayList<>();
        Map<Long, NoticePostCommentResponseDto> replyCommentMap = new HashMap<>();
        noticePostCommentResponseDtos.stream().forEach(
                c -> {
                    if(noticePostCommentIdsLikedByUser.contains(c.getCommentId())) c.likedByUser();
                    replyCommentMap.put(c.getCommentId(), c);
                    if (c.getParentCommentId() != null) replyCommentMap.get(c.getParentCommentId()).getChildrenComments().add(c);
                    else result.add(c);
                });
        return result;
    }

    private Map<Long, PostCommentVO> createNoticePostCommentHierarchyStructureMap(List<PostCommentVO> postCommentVO){
        Map<Long, PostCommentVO> commentsMap = new HashMap<>();
        postCommentVO.stream().forEach(
                c -> {
                    commentsMap.put(c.getCommentId(), c);
                    if(commentsMap.containsKey(c.getParentCommentId())) commentsMap.get(c.getParentCommentId()).addChild(c);
                }
        );
        return commentsMap;
    }

    private boolean checkAllMyChildNoticePostCommentIsDeleted(List<PostCommentVO> noticePostComments){
        for (PostCommentVO postCommentVO : noticePostComments) {
            if (!postCommentVO.isDeleted()) {
                return false;
            }
            if(!checkAllMyChildNoticePostCommentIsDeleted(postCommentVO.getChildrenComment())){
                return false;
            }
        }
        return true;
    }

    private void shouldParentCommentBeDeleted(Map<Long, PostCommentVO> noticePostCommentsMap, Long parentCommentId){
        if(noticePostCommentsMap.containsKey(parentCommentId)){
            PostCommentVO parentComment = noticePostCommentsMap.get(parentCommentId);
            if(parentComment.isDeleted() && checkAllMyChildNoticePostCommentIsDeleted(parentComment.getChildrenComment())){
                noticePostCommentRepository.deleteById(parentCommentId);
                if(parentComment.hasParentComment())
                    shouldParentCommentBeDeleted(noticePostCommentsMap, parentComment.getParentCommentId());
            }
        }
    }
}
