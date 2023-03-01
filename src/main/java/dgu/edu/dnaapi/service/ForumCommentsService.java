package dgu.edu.dnaapi.service;

import dgu.edu.dnaapi.domain.*;
import dgu.edu.dnaapi.domain.dto.comments.CommentsSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.comments.CommentsUpdateRequestDto;
import dgu.edu.dnaapi.domain.dto.forumComments.ForumCommentsResponseDto;
import dgu.edu.dnaapi.repository.forum.ForumCommentsRepository;
import dgu.edu.dnaapi.repository.forum.ForumsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForumCommentsService {
    private final ForumsRepository forumsRepository;
    private final ForumCommentsRepository forumCommentsRepository;

    @Transactional
    public Long save(User user, CommentsSaveRequestDto requestDto) {
        ForumComments comment = requestDto.toForumComments(user);
        comment.registerAuthor(user);

        Long forumId = requestDto.getPostId();

        Forums forum = forumsRepository.findById(forumId).orElseThrow(
                () -> new IllegalArgumentException("해당 글이 없습니다. id=" + forumId));
        comment.registerForum(forum);

        if(requestDto.getParentCommentId() != null) {
            Long parentCommentId = requestDto.getParentCommentId();
            ForumComments parent = forumCommentsRepository.findById(parentCommentId).orElseThrow(
                    () -> new IllegalArgumentException("해당 댓글이 없습니다. id=" + parentCommentId));
            comment.registerParent(parent);
        }

        return forumCommentsRepository.save(comment).getCommentId();
    }

    @Transactional
    public Long update(Long userId, CommentsUpdateRequestDto requestDto, Long forumCommentId) {
        ForumComments comment = forumCommentsRepository.findById(forumCommentId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글이 없습니다. id=" + forumCommentId));

        if(!comment.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("작성자만 댓글을 수정할 수 있습니다.");
        }
        comment.update(requestDto.getContent());
        return comment.getCommentId();
    }

    @Transactional
    public Long delete(Long userId, Long deleteForumCommentId) {
        // todo 나중에 대댓글 삭제 조건 처리 등 추가 해야함
        ForumComments comment = forumCommentsRepository.findById(deleteForumCommentId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글이 없습니다. id=" + deleteForumCommentId));

        if(!comment.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("작성자만 댓글을 삭제할 수 있습니다.");
        }
        forumCommentsRepository.deleteById(deleteForumCommentId);
        return deleteForumCommentId;
    }

    /**
     * Todo
     * : Filter 필요유무 확인
     * */
    public List<ForumCommentsResponseDto> findAllForumComments(Long forumId) {
        return forumCommentsRepository.findAllForumCommentsById(forumId)
                .stream().map(ForumCommentsResponseDto::new).collect(Collectors.toList());
//        return commentsList.stream()
//                .filter(c -> c.getForum().getForumId().equals(forumId))
//                .map(ForumCommentsResponseDto::new).collect(Collectors.toList());
    }
}
