package dgu.edu.dnaapi.repository.forum;


import dgu.edu.dnaapi.domain.ForumCommentsLikes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForumCommentsLikesRepository extends JpaRepository<ForumCommentsLikes, Long>, ForumCommentsLikesRepositoryCustom {
}
