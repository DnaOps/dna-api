package dgu.edu.dnaapi.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            // Todo : Fetch 시 forumID 가져오도록 fetch join 실시
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
        // todo 나중에 대댓글 삭제 조건 처리 등 추가 해야함
        ForumComments comment = forumCommentsRepository.findById(deleteForumCommentId).orElseThrow(
                () -> new DNACustomException("해당 댓글이 없습니다. id=" + deleteForumCommentId, DnaStatusCode.INVALID_COMMENT));

        if(!comment.getAuthor().getId().equals(userId)) {
            throw new DNACustomException("작성자만 댓글을 삭제할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        forumsRepository.decreaseCommentCount(comment.getForum().getForumId());
        forumCommentsRepository.deleteById(deleteForumCommentId);
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
        List<ForumCommentsResponseDto> result = createCommentHierarchyStructure(forumCommentsResponseDtos);
        return ListResponse.builder().list(result).hasNext(hasNext).build();
    }

    private List<ForumCommentsResponseDto> convertToForumCommentsResponseDto(List<ForumComments> commentsList) {
        return commentsList.stream().
                map(c -> ForumCommentsResponseDto.convertForumCommentToResponseDto(c)).collect(Collectors.toList());
    }

    private List<Long> getCommentIds(List<ForumComments> commentsList) {
        return commentsList.stream().map(c -> c.getCommentId()).collect(Collectors.toList());
    }

    private List<ForumCommentsResponseDto> createCommentHierarchyStructure(List<ForumCommentsResponseDto> forumCommentsResponseDtos){
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
}
