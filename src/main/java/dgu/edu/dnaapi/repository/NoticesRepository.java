package dgu.edu.dnaapi.repository;

import dgu.edu.dnaapi.domain.Notices;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticesRepository extends JpaRepository<Notices, Long> {

    @Override
    @EntityGraph(attributePaths = {"author"}, type = EntityGraph.EntityGraphType.FETCH)
    List<Notices> findAll();
}
