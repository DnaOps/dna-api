package dgu.edu.dnaapi.repository.forum;

public interface ForumLikesRepositoryCustom {

    boolean findForumLikesByUserIdAndForumId(Long forumId, Long userId);
}
