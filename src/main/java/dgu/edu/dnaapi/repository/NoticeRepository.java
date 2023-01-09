package dgu.edu.dnaapi.repository;

import dgu.edu.dnaapi.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
