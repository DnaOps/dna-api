package dgu.edu.dnaapi.repository.forum;

import dgu.edu.dnaapi.domain.ForumLikes;
import dgu.edu.dnaapi.domain.Forums;
import dgu.edu.dnaapi.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ForumLikesRepository extends JpaRepository<ForumLikes, Long>, ForumLikesRepositoryCustom {
    ForumLikes findByUserAndForum(User user, Forums forum);

    @Modifying(clearAutomatically = true)
    @Query("delete from ForumLikes f where f.forum.forumId = :forumId")
    int deleteForumLikesByForumId(@Param("forumId")Long forumId);
}
