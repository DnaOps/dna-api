package dgu.edu.dnaapi.repository.forum;


import dgu.edu.dnaapi.domain.ForumCommentsLikes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ForumCommentsLikesRepository extends JpaRepository<ForumCommentsLikes, Long>, ForumCommentsLikesRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ForumCommentsLikes l where l.forumComments.commentId in (:forumCommentsIds)")
    int deleteAllForumCommentsLikesInForumCommentsIds(@Param("forumCommentsIds")List<Long> forumCommentsIds);
}
