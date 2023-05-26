package dgu.edu.dnaapi.repository.forum;


import dgu.edu.dnaapi.controller.dto.BoardSearchCondition;
import dgu.edu.dnaapi.domain.dto.forum.ForumsMetaDataResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ForumsRepositoryCustom {

    List<ForumsMetaDataResponseDto> search(BoardSearchCondition condition, Pageable pageable);
}
