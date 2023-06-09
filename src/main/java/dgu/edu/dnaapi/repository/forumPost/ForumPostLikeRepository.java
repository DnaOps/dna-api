package dgu.edu.dnaapi.repository.forumPost;

import dgu.edu.dnaapi.domain.ForumPostLike;
import dgu.edu.dnaapi.domain.ForumPost;
import dgu.edu.dnaapi.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ForumPostLikeRepository extends JpaRepository<ForumPostLike, Long>, ForumPostLikeRepositoryCustom {

    Optional<ForumPostLike> findByUserAndForumPost(User user, ForumPost forumPost);

    @Modifying(clearAutomatically = true)
    @Query("delete from ForumPostLike f where f.forumPost.forumPostId = :forumPostId")
    int deleteAllForumPostLikesByForumPostId(@Param("forumPostId")Long forumPostId);
}
