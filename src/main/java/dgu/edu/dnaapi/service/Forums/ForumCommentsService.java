package dgu.edu.dnaapi.service.Forums;

import dgu.edu.dnaapi.controller.dto.CommentSearchCondition;
import dgu.edu.dnaapi.domain.*;
import dgu.edu.dnaapi.domain.dto.comments.CommentsSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.comments.CommentsUpdateRequestDto;
import dgu.edu.dnaapi.domain.dto.forumComments.ForumCommentsResponseDto;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.domain.response.ListResponse;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.forum.ForumCommentsRepository;
import dgu.edu.dnaapi.repository.forum.ForumsRepository;
import dgu.edu.dnaapi.service.Forums.vo.ForumCommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForumCommentsService {
    private final ForumsRepository forumsRepository;
    private final ForumCommentsRepository forumCommentsRepository;

    @Transactional
    public Long save(User user, CommentsSaveRequestDto requestDto) {
        ForumComments comment = requestDto.toForumComments(user);

        Long forumId = requestDto.getPostId();
        Forums forum = forumsRepository.findById(forumId).orElseThrow(
                () -> new DNACustomException("해당 글이 없습니다. id=" + forumId, DnaStatusCode.INVALID_POST));
        comment.registerForum(forum);

        if(requestDto.getParentCommentId() != null) {
            Long parentCommentId = requestDto.getParentCommentId();
            ForumComments parent = forumCommentsRepository.findByCommentIdWithForum(parentCommentId).orElseThrow(
                    () -> new DNACustomException("해당 댓글이 없습니다. id=" + parentCommentId, DnaStatusCode.INVALID_COMMENT));
            if(parent.getForum().getForumId() != forumId)
                    throw new DNACustomException("대댓글 부모 연관관계가 잘못되었습니다.", DnaStatusCode.INVALID_COMMENT);

            comment.registerParent(parent);
        }
        forumsRepository.increaseCommentCount(forumId);
        return forumCommentsRepository.save(comment).getCommentId();
    }

    @Transactional
    public Long update(Long userId, CommentsUpdateRequestDto requestDto, Long forumCommentId) {
        ForumComments comment = forumCommentsRepository.findById(forumCommentId).orElseThrow(
                () -> new DNACustomException("해당 댓글이 없습니다. id=" + forumCommentId, DnaStatusCode.INVALID_COMMENT));

        if(!comment.getAuthor().getId().equals(userId)) {
            throw new DNACustomException("작성자만 댓글을 수정할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        comment.update(requestDto.getContent());
        return comment.getCommentId();
    }

    @Transactional
    public Long delete(Long userId, Long deleteForumCommentId) {
        ForumComments comment = forumCommentsRepository.findByCommentIdWithAuthorAndForum(deleteForumCommentId).orElseThrow(
                () -> new DNACustomException("해당 댓글이 없습니다. id=" + deleteForumCommentId, DnaStatusCode.INVALID_COMMENT));
        if(comment.isDeleted())
            throw new DNACustomException("이미 삭제된 댓글입니다. id = " + deleteForumCommentId ,DnaStatusCode.INVALID_DELETE_COMMENT);
        if(!comment.getAuthor().getId().equals(userId)) {
            throw new DNACustomException("작성자만 댓글을 삭제할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }

        forumsRepository.decreaseCommentCount(comment.getForum().getForumId());
        Long commentGroupId = comment.getCommentGroupId();
        List<ForumComments> forumCommentsList = new ArrayList<>();
        if(commentGroupId == null){
            commentGroupId = comment.getCommentId();
            forumCommentsList.add(comment);
        }
        List<ForumComments> allReplyComments = forumCommentsRepository.findAllReplyCommentsByCommentGroupId(comment.getForum().getForumId(), commentGroupId);
        forumCommentsList.addAll(allReplyComments);
        List<ForumCommentVO> forumCommentVOList = forumCommentsList.stream().map(ForumCommentVO::convertToForumCommentVO).collect(Collectors.toList());

        Map<Long, ForumCommentVO> forumCommentsMap = createForumCommentHierarchyStructure(forumCommentVOList);

        ForumCommentVO deletedForumComment = forumCommentsMap.get(deleteForumCommentId);
        deletedForumComment.setDeletedStatus();
        if (checkAllDeleted(deletedForumComment.getChildrenComment())){
            forumCommentsRepository.deleteById(deleteForumCommentId);
        }else{
            forumCommentsRepository.softDeleted(deleteForumCommentId);
        }
        if(deletedForumComment.getParentCommentId() != null)
            deleteParentComment(forumCommentsMap, deletedForumComment.getParentCommentId());

        return deleteForumCommentId;
    }

    public ListResponse findAllForumComments(CommentSearchCondition commentSearchCondition, Long forumId, Pageable pageable) {

        List<ForumComments> commentsList = forumCommentsRepository.search(forumId, commentSearchCondition.getStart(), pageable);
        boolean hasNext = false;
        if(commentsList.size() > pageable.getPageSize()){
            hasNext = true;
            commentsList.remove(pageable.getPageSize());
        }

        List<Long> commentIds = getCommentIds(commentsList);
        List<ForumComments> allReplyComments = forumCommentsRepository.findAllReplyComments(forumId, commentIds);
        commentsList.addAll(allReplyComments);

        List<ForumCommentsResponseDto> forumCommentsResponseDtos = convertToForumCommentsResponseDto(commentsList);
        List<ForumCommentsResponseDto> result = createForumCommentResponseHierarchyStructure(forumCommentsResponseDtos);
        return ListResponse.builder().list(result).hasNext(hasNext).build();
    }

    private List<ForumCommentsResponseDto> convertToForumCommentsResponseDto(List<ForumComments> commentsList) {
        return commentsList.stream().
                map(c -> ForumCommentsResponseDto.convertForumCommentToResponseDto(c)).collect(Collectors.toList());
    }

    private List<Long> getCommentIds(List<ForumComments> commentsList) {
        return commentsList.stream().map(c -> c.getCommentId()).collect(Collectors.toList());
    }

    private List<ForumCommentsResponseDto> createForumCommentResponseHierarchyStructure(List<ForumCommentsResponseDto> forumCommentsResponseDtos){
        List<ForumCommentsResponseDto> result = new ArrayList<>();
        Map<Long, ForumCommentsResponseDto> replyCommentMap = new HashMap<>();
        forumCommentsResponseDtos.stream().forEach(
                c -> {
                    replyCommentMap.put(c.getCommentId(), c);
                    if (c.getParentCommentId() != null) replyCommentMap.get(c.getParentCommentId()).getChildrenComments().add(c);
                    else result.add(c);
                });
        return result;
    }

    private Map<Long, ForumCommentVO> createForumCommentHierarchyStructure(List<ForumCommentVO> forumCommentVO){
        Map<Long, ForumCommentVO> commentsMap = new HashMap<>();
        forumCommentVO.stream().forEach(
                c -> {
                    commentsMap.put(c.getCommentId(), c);
                    if(commentsMap.containsKey(c.getParentCommentId())) commentsMap.get(c.getParentCommentId()).addChild(c);
                }
        );
        return commentsMap;
    }

    private boolean checkAllDeleted(List<ForumCommentVO> forumComments){
        for (ForumCommentVO forumCommentVO : forumComments) {
            if (!forumCommentVO.isDeleted()) {
                return false;
            }
            if(!checkAllDeleted(forumCommentVO.getChildrenComment())){
                return false;
            }
        }
        return true;
    }

    private void dfs(List<ForumCommentVO> forumComments){
        System.out.println("forumComments.size() = " + forumComments.size());
        for(int i = 0; i < forumComments.size(); ++i){
            System.out.println(" i + forumComments.get(i).getCommentId() + forumComments.get(i).isDeleted() = " +  i + forumComments.get(i).getCommentId() + forumComments.get(i).isDeleted());
            dfs(forumComments.get(i).childrenComment);
        }
    }

    private void deleteParentComment(Map<Long, ForumCommentVO> forumCommentsMap, Long parentCommentId){
        if(forumCommentsMap.containsKey(parentCommentId))
            if(checkAllDeleted(forumCommentsMap.get(parentCommentId).getChildrenComment())){
                forumCommentsMap.get(parentCommentId).setDeletedStatus();
                forumCommentsRepository.deleteById(parentCommentId);
                if(forumCommentsMap.get(parentCommentId).getParentCommentId() != null)
                    deleteParentComment(forumCommentsMap, forumCommentsMap.get(parentCommentId).getParentCommentId());
            }
    }
}
