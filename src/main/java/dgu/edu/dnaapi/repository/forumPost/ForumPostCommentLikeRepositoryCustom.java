package dgu.edu.dnaapi.repository.forumPost;

import dgu.edu.dnaapi.domain.ForumPostComment;
import dgu.edu.dnaapi.domain.ForumPostCommentLike;
import dgu.edu.dnaapi.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ForumPostCommentLikeRepositoryCustom {

    boolean isForumPostLikedByUser(User user, ForumPostComment forumPostComment);
    Optional<ForumPostCommentLike> findForumPostCommentLikeByUserIdAndForumPostCommentId(Long userId, Long forumPostCommentId);
    long deleteAllForumPostCommentLikesByForumPostCommentId(Long forumPostCommentId);
    Set<Long> findForumPostCommentIdsLikedByUserId(Long userId, List<Long> forumPostCommentIds);
}
