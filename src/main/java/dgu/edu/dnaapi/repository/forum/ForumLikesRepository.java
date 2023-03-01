package dgu.edu.dnaapi.repository.forum;

import dgu.edu.dnaapi.domain.ForumLikes;
import dgu.edu.dnaapi.domain.Forums;
import dgu.edu.dnaapi.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForumLikesRepository extends JpaRepository<ForumLikes, Long> {
    ForumLikes findByUserAndForum(User user, Forums forum);
}
