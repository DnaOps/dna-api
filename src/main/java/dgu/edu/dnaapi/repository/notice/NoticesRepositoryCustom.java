package dgu.edu.dnaapi.repository.notice;

import dgu.edu.dnaapi.controller.dto.BoardSearchCondition;
import dgu.edu.dnaapi.domain.Notices;
import dgu.edu.dnaapi.domain.dto.notices.NoticesMetaDataResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface NoticesRepositoryCustom {

    List<NoticesMetaDataResponseDto> search(BoardSearchCondition condition, Pageable pageable);
    Optional<Notices> findByIdWithComments(Long noticeId);
}
