package dgu.edu.dnaapi.repository.forum;

import dgu.edu.dnaapi.domain.ForumComments;
import dgu.edu.dnaapi.domain.ForumCommentsLikes;
import dgu.edu.dnaapi.domain.User;

import java.util.List;
import java.util.Set;

public interface ForumCommentsLikesRepositoryCustom {

    boolean isForumLikedByUser(User user, ForumComments forumComments);
    ForumCommentsLikes findForumCommentsLikesByUserAndForumComments(Long userId, Long forumCommentsId);
    long deleteAllForumCommentsLikesByForumCommentsId(Long forumCommentsId);
    Set<Long> findForumCommentIdsLikedByUserId(Long userId, List<Long> commentIds);
}
