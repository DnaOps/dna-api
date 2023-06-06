package dgu.edu.dnaapi.repository.forum;

import dgu.edu.dnaapi.domain.ForumComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ForumCommentsRepository extends JpaRepository<ForumComments, Long>, ForumCommentsRepositoryCustom {

    //@Query("SELECT distinct c FROM ForumComments c join fetch c.forum")
    @Query("select c from ForumComments c left join fetch ForumComments d on c.parent.commentId = d.commentId join fetch c.forum where c.forum.forumId =:forumId")
    List<ForumComments> findAllForumCommentsById(@Param("forumId") Long forumId);

    @Query("select c from ForumComments c join fetch c.forum where c.commentId = :commentId")
    Optional<ForumComments> findByCommentIdWithForum(@Param("commentId") Long commentId);

    @Query("select c from ForumComments c join fetch c.author join fetch c.forum where c.commentId = :commentId")
    Optional<ForumComments> findByCommentIdWithAuthorAndForum(@Param("commentId") Long commentId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ForumComments c set c.isDeleted = true where c.commentId =:commentId")
    void softDeleted(@Param("commentId") Long commentId);

    @Modifying(clearAutomatically = true)
    @Query("update ForumComments c set c.likeCount = c.likeCount + 1 where c.commentId = :forumCommentId")
    int increaseLikeCount(@Param("forumCommentId") Long forumCommentId);

    @Modifying(clearAutomatically = true)
    @Query("update ForumComments c set c.likeCount = c.likeCount - 1 where c.commentId = :forumCommentId")
    int decreaseLikeCount(@Param("forumCommentId") Long forumCommentId);

}
