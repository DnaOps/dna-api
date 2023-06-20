package dgu.edu.dnaapi.service.studyPost;

import dgu.edu.dnaapi.controller.dto.CommentSearchCondition;
import dgu.edu.dnaapi.domain.*;
import dgu.edu.dnaapi.domain.dto.comment.CommentSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.comment.CommentUpdateRequestDto;
import dgu.edu.dnaapi.domain.dto.studyPostComment.StudyPostCommentResponseDto;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.domain.response.ListResponse;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.studyPost.StudyPostCommentLikeRepository;
import dgu.edu.dnaapi.repository.studyPost.StudyPostCommentRepository;
import dgu.edu.dnaapi.repository.studyPost.StudyPostRepository;
import dgu.edu.dnaapi.service.vo.PostCommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyPostCommentService {

    private final StudyPostCommentRepository studyPostCommentRepository;
    private final StudyPostCommentLikeRepository studyPostCommentLikeRepository;
    private final StudyPostRepository studyPostRepository;

    @Transactional
    public Long save(User user, CommentSaveRequestDto requestDto, Long studyPostId) {
        StudyPostComment studyPostComment = requestDto.toStudyPostComment(user);
        StudyPost studyPost = studyPostRepository.findById(studyPostId).orElseThrow(
                () -> new DNACustomException("해당 글이 없습니다. id=" + studyPostId, DnaStatusCode.INVALID_POST));
        studyPostComment.registerStudyPost(studyPost);

        if(requestDto.getParentCommentId() != null) {
            Long parentCommentId = requestDto.getParentCommentId();
            StudyPostComment parent = studyPostCommentRepository.findWithStudyPostByStudyPostCommentId(parentCommentId).orElseThrow(
                    () -> new DNACustomException("해당 댓글이 없습니다. id=" + parentCommentId, DnaStatusCode.INVALID_COMMENT));
            if(!parent.getStudyPost().getStudyPostId().equals(studyPostId))
                throw new DNACustomException("대댓글 부모 연관관계가 잘못되었습니다.", DnaStatusCode.INVALID_COMMENT);
            studyPostComment.registerParentStudyComment(parent);
        }
        studyPostRepository.increaseCommentCount(studyPostId);
        return studyPostCommentRepository.save(studyPostComment).getStudyPostCommentId();
    }

    @Transactional
    public Long update(Long userId, CommentUpdateRequestDto requestDto, Long studyPostCommentId, Long studyPostId) {
        StudyPostComment studyPostComment = studyPostCommentRepository.findWithStudyPostByStudyPostCommentId(studyPostCommentId).orElseThrow(
                () -> new DNACustomException("해당 댓글이 없습니다. id=" + studyPostCommentId, DnaStatusCode.INVALID_COMMENT));
        if(!studyPostId.equals(studyPostComment.getStudyPost().getStudyPostId())){
            throw new DNACustomException("해당 댓글이 해당 게시글에 존재하지 않습니다.", DnaStatusCode.INVALID_COMMENT);
        }
        if(!studyPostComment.getAuthor().getId().equals(userId)) {
            throw new DNACustomException("작성자만 댓글을 수정할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        studyPostComment.update(requestDto.getContent());
        return studyPostComment.getStudyPostCommentId();
    }

    public ListResponse findAllStudyPostCommentsByCommentSearchCondition(CommentSearchCondition commentSearchCondition, Long studyPostId, Pageable pageable) {
        List<StudyPostComment> studyPostComments = studyPostCommentRepository.search(studyPostId, commentSearchCondition.getStart(), pageable);
        boolean hasNext = isHasNext(pageable.getPageSize(), studyPostComments);

        List<Long> commentIds = getStudyPostCommentIds(studyPostComments);
        studyPostComments.addAll(studyPostCommentRepository.findAllReplyStudyPostComments(studyPostId, commentIds));
        List<StudyPostCommentResponseDto> studyPostCommentResponseDtoList = convertToStudyPostCommentResponseDto(studyPostComments);
        Set<Long> studyCommentIdsLikedByUser = new HashSet<>();
        if(commentSearchCondition.hasUserId()){
            studyCommentIdsLikedByUser = getStudyPostCommentIdsLikedByUser(commentSearchCondition.getUserId(), getStudyPostCommentIds(studyPostComments));
        }
        List<StudyPostCommentResponseDto> result = createStudyPostCommentResponseHierarchyStructure(studyPostCommentResponseDtoList, studyCommentIdsLikedByUser);
        return ListResponse.builder()
                .list(result)
                .hasNext(hasNext)
                .build();
    }

    private boolean isHasNext(int pageSize, List<StudyPostComment> commentsList) {
        if(commentsList.size() > pageSize){
            commentsList.remove(pageSize);
            return true;
        }
        return false;
    }

    private Set<Long> getStudyPostCommentIdsLikedByUser(Long userId, List<Long> allStudyCommentIds) {
        return studyPostCommentLikeRepository.findStudyPostCommentIdsLikedByUserId(userId, allStudyCommentIds);
    }

    private List<StudyPostCommentResponseDto> convertToStudyPostCommentResponseDto(List<StudyPostComment> commentsList) {
        return commentsList.stream().
                map(StudyPostCommentResponseDto::convertStudyPostCommentToResponseDto).collect(Collectors.toList());
    }

    private List<Long> getStudyPostCommentIds(List<StudyPostComment> commentsList) {
        return commentsList.stream().map(StudyPostComment::getStudyPostCommentId).collect(Collectors.toList());
    }

    private List<StudyPostCommentResponseDto> createStudyPostCommentResponseHierarchyStructure(List<StudyPostCommentResponseDto> studyPostCommentResponseDtos, Set<Long> studyPostCommentIdsLikedByUser){
        List<StudyPostCommentResponseDto> result = new ArrayList<>();
        Map<Long, StudyPostCommentResponseDto> replyCommentMap = new HashMap<>();
        studyPostCommentResponseDtos.stream().forEach(
                c -> {
                    if(studyPostCommentIdsLikedByUser.contains(c.getCommentId())) c.likedByUser();
                    replyCommentMap.put(c.getCommentId(), c);
                    if (c.getParentCommentId() != null) replyCommentMap.get(c.getParentCommentId()).getChildrenComments().add(c);
                    else result.add(c);
                });
        return result;
    }

    @Transactional
    public Long delete(Long userId, Long deleteStudyPostCommentId, Long studyPostId) {
        StudyPostComment studyPostComment = studyPostCommentRepository.findWithAuthorAndStudyPostByStudyPostCommentId(deleteStudyPostCommentId).orElseThrow(
                () -> new DNACustomException("해당 댓글이 없습니다. id=" + deleteStudyPostCommentId, DnaStatusCode.INVALID_COMMENT));
        if(studyPostComment.isDeleted())
            throw new DNACustomException("이미 삭제된 댓글입니다. id = " + deleteStudyPostCommentId ,DnaStatusCode.INVALID_DELETE_COMMENT);
        if(!studyPostComment.getAuthor().getId().equals(userId)) {
            throw new DNACustomException("작성자만 댓글을 삭제할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        if (!studyPostId.equals(studyPostComment.getStudyPost().getStudyPostId())) {
            throw new DNACustomException("해당 댓글이 해당 게시글에 존재하지 않습니다.", DnaStatusCode.INVALID_COMMENT);
        }

        studyPostRepository.decreaseCommentCount(studyPostComment.getStudyPost().getStudyPostId());
        Long commentGroupId = studyPostComment.getCommentGroupId();
        List<StudyPostComment> studyPostCommentList = new ArrayList<>();
        if(commentGroupId == null){
            commentGroupId = studyPostComment.getStudyPostCommentId();
            studyPostCommentList.add(studyPostComment);
        }
        List<StudyPostComment> allReplyComments = studyPostCommentRepository.findAllReplyStudyPostCommentsByCommentGroupId(studyPostComment.getStudyPost().getStudyPostId(), commentGroupId);
        studyPostCommentList.addAll(allReplyComments);
        List<PostCommentVO> postCommentVOList = studyPostCommentList.stream().map(PostCommentVO::convertToPostCommentVO).collect(Collectors.toList());

        Map<Long, PostCommentVO> studyPostCommentsMap = createStudyPostCommentHierarchyStructureMap(postCommentVOList);

        PostCommentVO deletedPostCommentVO = studyPostCommentsMap.get(deleteStudyPostCommentId);
        deletedPostCommentVO.setDeletedStatus();
        if (checkAllMyChildStudyPostCommentIsDeleted(deletedPostCommentVO.getChildrenComment())){
            studyPostCommentRepository.deleteById(deleteStudyPostCommentId);
        }else{
            studyPostCommentRepository.softDelete(deleteStudyPostCommentId);
        }
        studyPostCommentLikeRepository.deleteAllStudyPostCommentLikesByStudyPostCommentId(deleteStudyPostCommentId);
        if(deletedPostCommentVO.hasParentComment())
            shouldParentCommentBeDeleted(studyPostCommentsMap, deletedPostCommentVO.getParentCommentId());

        return deleteStudyPostCommentId;
    }

    private Map<Long, PostCommentVO> createStudyPostCommentHierarchyStructureMap(List<PostCommentVO> postCommentVO){
        Map<Long, PostCommentVO> commentsMap = new HashMap<>();
        postCommentVO.stream().forEach(
                c -> {
                    commentsMap.put(c.getCommentId(), c);
                    if(commentsMap.containsKey(c.getParentCommentId())) commentsMap.get(c.getParentCommentId()).addChild(c);
                }
        );
        return commentsMap;
    }

    private boolean checkAllMyChildStudyPostCommentIsDeleted(List<PostCommentVO> studyPostComments){
        for (PostCommentVO postCommentVO : studyPostComments) {
            if (!postCommentVO.isDeleted()) {
                return false;
            }
            if(!checkAllMyChildStudyPostCommentIsDeleted(postCommentVO.getChildrenComment())){
                return false;
            }
        }
        return true;
    }

    private void shouldParentCommentBeDeleted(Map<Long, PostCommentVO> studyPostCommentsMap, Long parentCommentId){
        if(studyPostCommentsMap.containsKey(parentCommentId)){
            PostCommentVO parentComment = studyPostCommentsMap.get(parentCommentId);
            if(parentComment.isDeleted() && checkAllMyChildStudyPostCommentIsDeleted(parentComment.getChildrenComment())){
                studyPostCommentRepository.deleteById(parentCommentId);
                if(parentComment.hasParentComment())
                    shouldParentCommentBeDeleted(studyPostCommentsMap, parentComment.getParentCommentId());
            }
        }
    }
}
