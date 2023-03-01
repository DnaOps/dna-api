package dgu.edu.dnaapi.repository.forum;

import dgu.edu.dnaapi.domain.Forums;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumsRepository extends JpaRepository<Forums, Long> {

    @Override
    @EntityGraph(attributePaths = {"author"}, type = EntityGraph.EntityGraphType.FETCH)
    List<Forums> findAll();
}
