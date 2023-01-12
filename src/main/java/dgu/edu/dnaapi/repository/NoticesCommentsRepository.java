package dgu.edu.dnaapi.repository;

import dgu.edu.dnaapi.domain.NoticeComments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticesCommentsRepository  extends JpaRepository<NoticeComments, Long> {
}
