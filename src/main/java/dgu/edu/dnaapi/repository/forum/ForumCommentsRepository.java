package dgu.edu.dnaapi.repository.forum;

import dgu.edu.dnaapi.domain.ForumComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ForumCommentsRepository extends JpaRepository<ForumComments, Long> {

    @Query("SELECT distinct c FROM ForumComments c join fetch c.forum")
    List<ForumComments> findAllForumCommentsById(Long forumId);
}
