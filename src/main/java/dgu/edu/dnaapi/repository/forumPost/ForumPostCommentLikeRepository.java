package dgu.edu.dnaapi.repository.forumPost;


import dgu.edu.dnaapi.domain.ForumPostCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ForumPostCommentLikeRepository extends JpaRepository<ForumPostCommentLike, Long>, ForumPostCommentLikeRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ForumPostCommentLike l where l.forumPostComment.forumPostCommentId in (:forumCommentsIds)")
    int deleteAllForumCommentsLikesInForumCommentsIds(@Param("forumCommentsIds")List<Long> forumCommentsIds);
}
