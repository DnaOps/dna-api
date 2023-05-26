package dgu.edu.dnaapi.repository.forum;

import dgu.edu.dnaapi.domain.Forums;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ForumsRepository extends JpaRepository<Forums, Long>, ForumsRepositoryCustom {

    @Override
    @EntityGraph(attributePaths = {"author"}, type = EntityGraph.EntityGraphType.FETCH)
    List<Forums> findAll();

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Forums f set f.likeCount = f.likeCount + 1 where f.forumId = :forumId")
    int increaseLikeCount(@Param("forumId")Long forumId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Forums f set f.likeCount = f.likeCount - 1 where f.forumId = :forumId")
    int decreaseLikeCount(@Param("forumId")Long forumId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Forums f set f.commentCount = f.commentCount + 1 where f.forumId = :forumId")
    int increaseCommentCount(@Param("forumId")Long forumId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Forums f set f.commentCount = f.commentCount - 1 where f.forumId = :forumId")
    int decreaseCommentCount(@Param("forumId")Long forumId);
}
