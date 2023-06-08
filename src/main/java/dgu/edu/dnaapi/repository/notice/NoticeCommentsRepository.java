package dgu.edu.dnaapi.repository.notice;

import dgu.edu.dnaapi.domain.NoticeComments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeCommentsRepository extends JpaRepository<NoticeComments, Long> {
}
