package dgu.edu.dnaapi.repository.forumPost;

import dgu.edu.dnaapi.domain.ForumPost;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ForumPostRepository extends JpaRepository<ForumPost, Long>, ForumPostRepositoryCustom {

    @Override
    @EntityGraph(attributePaths = {"author"}, type = EntityGraph.EntityGraphType.FETCH)
    List<ForumPost> findAll();

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ForumPost f set f.likeCount = f.likeCount + 1 where f.forumPostId = :forumPostId")
    int increaseLikeCount(@Param("forumPostId") Long forumPostId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ForumPost f set f.likeCount = f.likeCount - 1 where f.forumPostId = :forumPostId")
    int decreaseLikeCount(@Param("forumPostId") Long forumPostId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ForumPost f set f.commentCount = f.commentCount + 1 where f.forumPostId = :forumPostId")
    int increaseCommentCount(@Param("forumPostId") Long forumPostId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ForumPost f set f.commentCount = f.commentCount - 1 where f.forumPostId = :forumPostId")
    int decreaseCommentCount(@Param("forumPostId") Long forumPostId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ForumPost f WHERE f.forumPostId = :forumPostId")
    int deleteForumPostByForumPostId(@Param("forumPostId") Long forumPostId);
}
