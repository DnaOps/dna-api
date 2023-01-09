package dgu.edu.dnaapi.service;

import dgu.edu.dnaapi.domain.dto.NoticesSaveRequestDto;
import dgu.edu.dnaapi.repository.NoticesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class NoticesService {
    private final NoticesRepository noticesRepository;

    @Transactional
    public Long save(NoticesSaveRequestDto requestDto) {
        return noticesRepository.save(requestDto.toEntity())
                .getId();
    }
}
