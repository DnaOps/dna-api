package dgu.edu.dnaapi.service.ForumPost;

import dgu.edu.dnaapi.domain.ForumPostComment;
import dgu.edu.dnaapi.domain.ForumPostCommentLike;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.forumPost.ForumPostCommentLikeRepository;
import dgu.edu.dnaapi.repository.forumPost.ForumPostCommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class ForumPostCommentLikeService {

    private final ForumPostCommentRepository forumPostCommentRepository;
    private final ForumPostCommentLikeRepository forumPostCommentLikeRepository;

    public boolean isForumCommentLikedByUser(User user, ForumPostComment forumPostComment){
        return forumPostCommentLikeRepository.isForumPostLikedByUser(user, forumPostComment);
    }

    @Transactional
    public String like(User user, Long forumPostId, Long forumPostCommentId) {
        ForumPostComment forumPostComment = getForumPostComment(forumPostCommentId);
        checkForumPostCommentIsInForumPost(forumPostId, forumPostCommentId, forumPostComment);

        forumPostCommentLikeRepository.findForumPostCommentLikeByUserIdAndForumPostCommentId(user.getId(), forumPostCommentId).ifPresent(
                (forumPostCommentLike) -> {
                    throw new DNACustomException("이미 해당 유저가 해당 댓글을 좋아합니다.", DnaStatusCode.INVALID_LIKE_OPERATION);
                }
        );
        return likeForumComments(user, forumPostComment);
    }

    @Transactional
    public String dislike(User user, Long forumPostId, Long forumPostCommentId) {
        ForumPostComment forumPostComment = getForumPostComment(forumPostCommentId);
        checkForumPostCommentIsInForumPost(forumPostId, forumPostCommentId, forumPostComment);

        ForumPostCommentLike forumPostCommentLike = forumPostCommentLikeRepository.findForumPostCommentLikeByUserIdAndForumPostCommentId(user.getId(), forumPostCommentId).orElseThrow(
                () -> new DNACustomException("현재 해당 유저가 해당 댓글을 좋아하지 않습니다.", DnaStatusCode.INVALID_LIKE_OPERATION));

        return disLikeForumComments(forumPostCommentLike);
    }

    private void checkForumPostCommentIsInForumPost(Long forumPostId, Long forumPostCommentId, ForumPostComment forumPostComment) {
        if(!forumPostComment.getForumPost().getForumPostId().equals(forumPostId))
            throw new DNACustomException("해당 게시글에 해당 댓글이 존재하지 않습니다. forumPostId = " + forumPostId + "commentId = " + forumPostCommentId, DnaStatusCode.INVALID_COMMENT);
    }

    private ForumPostComment getForumPostComment(Long forumPostCommentId) {
        ForumPostComment forumPostComment = forumPostCommentRepository.findWithForumPostByForumPostCommentId(forumPostCommentId).orElseThrow(
                () -> new DNACustomException("해당 댓글이 존재하지 않습니다. id = " + forumPostCommentId, DnaStatusCode.INVALID_COMMENT));
        if(forumPostComment.isDeleted())
            throw new DNACustomException("해당 댓글이 존재하지 않습니다. id = " + forumPostCommentId, DnaStatusCode.INVALID_DELETE_COMMENT);
        return forumPostComment;
    }

    private String disLikeForumComments(ForumPostCommentLike forumPostCommentLike) {
        forumPostCommentRepository.decreaseLikeCount(forumPostCommentLike.getForumPostComment().getForumPostCommentId());
        forumPostCommentLikeRepository.delete(forumPostCommentLike);
        return "deleted";
    }

    private String likeForumComments(User user, ForumPostComment forumPostComment) {
        forumPostCommentRepository.increaseLikeCount(forumPostComment.getForumPostCommentId());
        forumPostCommentLikeRepository.save(new ForumPostCommentLike(forumPostComment, user));
        return "added";
    }
}
