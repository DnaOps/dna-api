package dgu.edu.dnaapi.service;

import dgu.edu.dnaapi.domain.Notices;
import dgu.edu.dnaapi.domain.dto.NoticesResponseDto;
import dgu.edu.dnaapi.domain.dto.NoticesSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.NoticesUpdateRequestDto;
import dgu.edu.dnaapi.repository.NoticesRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Not;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NoticesService {
    private final NoticesRepository noticesRepository;

    @Transactional
    public Long save(NoticesSaveRequestDto requestDto) {
        return noticesRepository.save(requestDto.toEntity()).getNoticeId();
    }

    @Transactional
    public Long update(NoticesUpdateRequestDto requestDto) {
        Long id = requestDto.getNoticeId();
        Notices notices = noticesRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
        notices.update(requestDto.getTitle(), requestDto.getContent());
        return id;
    }

    public NoticesResponseDto findById(Long id) {
        Notices entity = noticesRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
        return new NoticesResponseDto(entity);
    }

    @Transactional
    public Long delete(NoticesUpdateRequestDto requestDto) {
        Long deleteId = requestDto.getNoticeId();
        noticesRepository.deleteById(deleteId);

        return deleteId;
    }

    public List<NoticesResponseDto> findAll() {
        List<Notices> noticesList = noticesRepository
                .findAll(Sort.by(Sort.Direction.DESC, "noticeId"));
        return noticesList.stream().map(NoticesResponseDto::new).collect(Collectors.toList());
    }
}
