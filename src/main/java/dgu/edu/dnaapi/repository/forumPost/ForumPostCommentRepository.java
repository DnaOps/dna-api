package dgu.edu.dnaapi.repository.forumPost;

import dgu.edu.dnaapi.domain.ForumPostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ForumPostCommentRepository extends JpaRepository<ForumPostComment, Long>, ForumPostCommentRepositoryCustom {

    //@Query("SELECT distinct c FROM ForumComments c join fetch c.forum")
    @Query("select c from ForumPostComment c left join fetch ForumPostComment d on c.parent.forumPostCommentId = d.forumPostCommentId join fetch c.forumPost where c.forumPost.forumPostId =:forumPostId")
    List<ForumPostComment> findAllForumCommentsById(@Param("forumPostId") Long forumPostId);

    @Query("select c from ForumPostComment c join fetch c.forumPost where c.forumPostCommentId = :forumPostCommentId")
    Optional<ForumPostComment> findWithForumPostByForumPostCommentId(@Param("forumPostCommentId") Long forumPostCommentId);

    @Query("select c from ForumPostComment c join fetch c.author join fetch c.forumPost where c.forumPostCommentId = :forumPostCommentId")
    Optional<ForumPostComment> findWithAuthorAndForumPostByForumPostCommentId(@Param("forumPostCommentId") Long forumPostCommentId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ForumPostComment c set c.isDeleted = true where c.forumPostCommentId =:forumPostCommentId")
    void softDelete(@Param("forumPostCommentId") Long forumPostCommentId);

    @Modifying(clearAutomatically = true)
    @Query("update ForumPostComment c set c.likeCount = c.likeCount + 1 where c.forumPostCommentId = :forumPostCommentId")
    int increaseLikeCount(@Param("forumPostCommentId") Long forumPostCommentId);

    @Modifying(clearAutomatically = true)
    @Query("update ForumPostComment c set c.likeCount = c.likeCount - 1 where c.forumPostCommentId = :forumPostCommentId")
    int decreaseLikeCount(@Param("forumPostCommentId") Long forumPostCommentId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ForumPostComment c where c.forumPostCommentId = :forumPostCommentId")
    int deleteForumPostCommentByForumPostCommentId(@Param("forumPostCommentId") Long forumPostCommentId);
}
