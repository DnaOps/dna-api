package dgu.edu.dnaapi.repository.forumPost;

public interface ForumPostLikeRepositoryCustom {

    boolean findForumPostLikeByForumPostIdAndUserId(Long forumPostId, Long userId);
}
