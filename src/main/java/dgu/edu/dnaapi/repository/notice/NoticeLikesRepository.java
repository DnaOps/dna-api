package dgu.edu.dnaapi.repository.notice;

import dgu.edu.dnaapi.domain.NoticeLikes;
import dgu.edu.dnaapi.domain.Notices;
import dgu.edu.dnaapi.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface NoticeLikesRepository extends JpaRepository<NoticeLikes, Long> {
    NoticeLikes findByUserAndNotice(User user, Notices notice);
}