package dgu.edu.dnaapi.service.ForumPost;

import dgu.edu.dnaapi.controller.dto.CommentSearchCondition;
import dgu.edu.dnaapi.domain.*;
import dgu.edu.dnaapi.domain.dto.comment.CommentSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.comment.CommentUpdateRequestDto;
import dgu.edu.dnaapi.domain.dto.forumPostComment.ForumPostCommentResponseDto;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.domain.response.ListResponse;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.forumPost.ForumPostCommentLikeRepository;
import dgu.edu.dnaapi.repository.forumPost.ForumPostCommentRepository;
import dgu.edu.dnaapi.repository.forumPost.ForumPostRepository;
import dgu.edu.dnaapi.service.vo.PostCommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForumPostCommentService {
    private final ForumPostRepository forumPostRepository;
    private final ForumPostCommentRepository forumPostCommentRepository;
    private final ForumPostCommentLikeRepository forumPostCommentLikeRepository;

    @Transactional
    public Long save(User user, CommentSaveRequestDto requestDto, Long forumPostId) {
        ForumPostComment comment = requestDto.toForumComment(user);
        ForumPost forumPost = forumPostRepository.findById(forumPostId).orElseThrow(
                () -> new DNACustomException("해당 글이 없습니다. id=" + forumPostId, DnaStatusCode.INVALID_POST));
        comment.registerForumPost(forumPost);

        if(requestDto.getParentCommentId() != null) {
            Long parentCommentId = requestDto.getParentCommentId();
            ForumPostComment parent = forumPostCommentRepository.findWithForumPostByForumPostCommentId(parentCommentId).orElseThrow(
                    () -> new DNACustomException("해당 댓글이 없습니다. id=" + parentCommentId, DnaStatusCode.INVALID_COMMENT));
            if(parent.getForumPost().getForumPostId() != forumPostId)
                throw new DNACustomException("대댓글 부모 연관관계가 잘못되었습니다.", DnaStatusCode.INVALID_COMMENT);

            comment.registerParentForumPostComment(parent);
        }
        forumPostRepository.increaseCommentCount(forumPostId);
        return forumPostCommentRepository.save(comment).getForumPostCommentId();
    }

    @Transactional
    public Long update(Long userId, CommentUpdateRequestDto requestDto, Long forumCommentId, Long forumPostId) {
        ForumPostComment comment = forumPostCommentRepository.findWithForumPostByForumPostCommentId(forumCommentId).orElseThrow(
                () -> new DNACustomException("해당 댓글이 없습니다. id=" + forumCommentId, DnaStatusCode.INVALID_COMMENT));
        if(!forumPostId.equals(comment.getForumPost().getForumPostId())){
            throw new DNACustomException("해당 댓글이 해당 게시글에 존재하지 않습니다.", DnaStatusCode.INVALID_COMMENT);
        }
        if(!comment.getAuthor().getId().equals(userId)) {
            throw new DNACustomException("작성자만 댓글을 수정할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        comment.update(requestDto.getContent());
        return comment.getForumPostCommentId();
    }

    @Transactional
    public Long delete(Long userId, Long deleteForumPostCommentId, Long forumPostId) {
        ForumPostComment comment = forumPostCommentRepository.findWithAuthorAndForumPostByForumPostCommentId(deleteForumPostCommentId).orElseThrow(
                () -> new DNACustomException("해당 댓글이 없습니다. id=" + deleteForumPostCommentId, DnaStatusCode.INVALID_COMMENT));
        if(comment.isDeleted())
            throw new DNACustomException("이미 삭제된 댓글입니다. id = " + deleteForumPostCommentId ,DnaStatusCode.INVALID_DELETE_COMMENT);
        if(!comment.getAuthor().getId().equals(userId)) {
            throw new DNACustomException("작성자만 댓글을 삭제할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        if (!forumPostId.equals(comment.getForumPost().getForumPostId())) {
            throw new DNACustomException("해당 댓글이 해당 게시글에 존재하지 않습니다.", DnaStatusCode.INVALID_COMMENT);
        }

        forumPostRepository.decreaseCommentCount(comment.getForumPost().getForumPostId());
        Long commentGroupId = comment.getCommentGroupId();
        List<ForumPostComment> forumPostCommentList = new ArrayList<>();
        if(commentGroupId == null){
            commentGroupId = comment.getForumPostCommentId();
            forumPostCommentList.add(comment);
        }
        List<ForumPostComment> allReplyComments = forumPostCommentRepository.findAllReplyForumPostCommentsByCommentGroupId(comment.getForumPost().getForumPostId(), commentGroupId);
        forumPostCommentList.addAll(allReplyComments);
        List<PostCommentVO> postCommentVOList = forumPostCommentList.stream().map(PostCommentVO::convertToPostCommentVO).collect(Collectors.toList());

        Map<Long, PostCommentVO> forumCommentsMap = createForumPostCommentHierarchyStructure(postCommentVOList);

        PostCommentVO deletedPostCommentVO = forumCommentsMap.get(deleteForumPostCommentId);
        deletedPostCommentVO.setDeletedStatus();
        if (checkAllMyChildForumPostCommentIsDeleted(deletedPostCommentVO.getChildrenComment())){
            forumPostCommentRepository.deleteById(deleteForumPostCommentId);
        }else{
            forumPostCommentRepository.softDelete(deleteForumPostCommentId);
        }
        forumPostCommentLikeRepository.deleteAllForumPostCommentLikesByForumPostCommentId(deleteForumPostCommentId);
        if(deletedPostCommentVO.hasParentComment())
            shouldParentCommentBeDeleted(forumCommentsMap, deletedPostCommentVO.getParentCommentId());

        return deleteForumPostCommentId;
    }

    public ListResponse findAllForumComments(CommentSearchCondition commentSearchCondition, Long forumPostId, Pageable pageable) {

        List<ForumPostComment> commentsList = forumPostCommentRepository.search(forumPostId, commentSearchCondition.getStart(), pageable);
        boolean hasNext = isHasNext(pageable.getPageSize(), commentsList);

        List<Long> commentIds = getForumPostCommentIds(commentsList);
        commentsList.addAll(forumPostCommentRepository.findAllReplyForumPostComments(forumPostId, commentIds));
        List<ForumPostCommentResponseDto> forumPostCommentResponseDtoList = convertToForumPostCommentResponseDto(commentsList);
        Set<Long> forumCommentIdsLikedByUser = new HashSet<>();
        if(commentSearchCondition.hasUserId()){
            forumCommentIdsLikedByUser = getForumPostCommentIdsLikedByUser(commentSearchCondition.getUserId(), getForumPostCommentIds(commentsList));
        }
        List<ForumPostCommentResponseDto> result = createForumPostCommentResponseHierarchyStructure(forumPostCommentResponseDtoList, forumCommentIdsLikedByUser);
        return ListResponse.builder().list(result).hasNext(hasNext).build();
    }

    private Set<Long> getForumPostCommentIdsLikedByUser(Long userId, List<Long> allForumCommentIds) {
        return forumPostCommentLikeRepository.findForumPostCommentIdsLikedByUserId(userId, allForumCommentIds);
    }

    private boolean isHasNext(int pageSize, List<ForumPostComment> commentsList) {
        if(commentsList.size() > pageSize){
            commentsList.remove(pageSize);
            return true;
        }
        return false;
    }

    private List<ForumPostCommentResponseDto> convertToForumPostCommentResponseDto(List<ForumPostComment> commentsList) {
        return commentsList.stream().
                map(c -> ForumPostCommentResponseDto.convertForumCommentToResponseDto(c)).collect(Collectors.toList());
    }

    private List<Long> getForumPostCommentIds(List<ForumPostComment> commentsList) {
        return commentsList.stream().map(ForumPostComment::getForumPostCommentId).collect(Collectors.toList());
    }

    private List<ForumPostCommentResponseDto> createForumPostCommentResponseHierarchyStructure(List<ForumPostCommentResponseDto> forumPostCommentResponseDtos, Set<Long> forumCommentIdsLikedByUser){
        List<ForumPostCommentResponseDto> result = new ArrayList<>();
        Map<Long, ForumPostCommentResponseDto> replyCommentMap = new HashMap<>();
        forumPostCommentResponseDtos.stream().forEach(
                c -> {
                    if(forumCommentIdsLikedByUser.contains(c.getCommentId())) c.likedByUser();
                    replyCommentMap.put(c.getCommentId(), c);
                    if (c.getParentCommentId() != null) replyCommentMap.get(c.getParentCommentId()).getChildrenComments().add(c);
                    else result.add(c);
                });
        return result;
    }

    private Map<Long, PostCommentVO> createForumPostCommentHierarchyStructure(List<PostCommentVO> postCommentVO){
        Map<Long, PostCommentVO> commentsMap = new HashMap<>();
        postCommentVO.stream().forEach(
                c -> {
                    commentsMap.put(c.getCommentId(), c);
                    if(commentsMap.containsKey(c.getParentCommentId())) commentsMap.get(c.getParentCommentId()).addChild(c);
                }
        );
        return commentsMap;
    }

    private boolean checkAllMyChildForumPostCommentIsDeleted(List<PostCommentVO> forumComments){
        for (PostCommentVO postCommentVO : forumComments) {
            if (!postCommentVO.isDeleted()) {
                return false;
            }
            if(!checkAllMyChildForumPostCommentIsDeleted(postCommentVO.getChildrenComment())){
                return false;
            }
        }
        return true;
    }

    private void dfs(List<PostCommentVO> forumComments){
        System.out.println("forumComments.size() = " + forumComments.size());
        for(int i = 0; i < forumComments.size(); ++i){
            System.out.println(" i + forumComments.get(i).getCommentId() + forumComments.get(i).isDeleted() = " +  i + forumComments.get(i).getCommentId() + forumComments.get(i).isDeleted());
            dfs(forumComments.get(i).childrenComment);
        }
    }

    private void shouldParentCommentBeDeleted(Map<Long, PostCommentVO> forumCommentsMap, Long parentCommentId){
        if(forumCommentsMap.containsKey(parentCommentId)){
            PostCommentVO parentComment = forumCommentsMap.get(parentCommentId);
            if(parentComment.isDeleted() && checkAllMyChildForumPostCommentIsDeleted(parentComment.getChildrenComment())){
                forumPostCommentRepository.deleteById(parentCommentId);
                if(parentComment.hasParentComment())
                    shouldParentCommentBeDeleted(forumCommentsMap, parentComment.getParentCommentId());
            }
        }
    }
}
