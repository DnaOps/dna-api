package dgu.edu.dnaapi.service;

import dgu.edu.dnaapi.domain.Notices;
import dgu.edu.dnaapi.domain.dto.notices.NoticesDeleteRequestDto;
import dgu.edu.dnaapi.domain.dto.notices.NoticesResponseDto;
import dgu.edu.dnaapi.domain.dto.notices.NoticesUpdateRequestDto;
import dgu.edu.dnaapi.repository.NoticesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NoticesService {
    private final NoticesRepository noticesRepository;

    @Transactional
    public Long save(Notices notices) {
        return noticesRepository.save(notices).getNoticeId();
    }

    @Transactional
    public Long update(NoticesUpdateRequestDto requestDto, Long userId) {
        Long requestNoticeId = requestDto.getNoticeId();
        Notices notices = noticesRepository.findById(requestNoticeId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + requestNoticeId));
        // 게시글의 Author 만 게시글을 변경할 수 있도록 검사
        if (userId != notices.getAuthor().getId()){
            throw new IllegalArgumentException("게시글의 작성자만 수정할 수 있습니다.");
        }
        notices.update(requestDto.getTitle(), requestDto.getContent());
        return requestNoticeId;
    }

    public Notices findById(Long id) {
        return noticesRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
    }

    @Transactional
    public Long delete(NoticesDeleteRequestDto requestDto, Long userId) {
        Long deleteId = requestDto.getNoticeId();
        Notices entity = noticesRepository.findById(deleteId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + deleteId));
        if(entity.getAuthor().getId() != userId) {
            throw new IllegalArgumentException("해당 게시글의 작성자만 게시글을 삭제할 수 있습니다.");
        }
        noticesRepository.deleteById(deleteId);
        return deleteId;
    }

    public List<NoticesResponseDto> findAll() {
        List<Notices> noticesList = noticesRepository.findAll();
        return noticesList.stream().map(NoticesResponseDto::new).collect(Collectors.toList());
    }
}
