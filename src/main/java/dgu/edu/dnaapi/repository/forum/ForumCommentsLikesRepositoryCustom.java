package dgu.edu.dnaapi.repository.forum;

import dgu.edu.dnaapi.domain.ForumComments;
import dgu.edu.dnaapi.domain.ForumCommentsLikes;
import dgu.edu.dnaapi.domain.User;

public interface ForumCommentsLikesRepositoryCustom {

    boolean isForumLikedByUser(User user, ForumComments forumComments);
    ForumCommentsLikes findForumCommentsLikesByUserAndForumComments(Long userId, Long forumCommentsId);
    long deleteAllForumCommentsLikesByForumCommentsId(Long forumCommentsId);
}
