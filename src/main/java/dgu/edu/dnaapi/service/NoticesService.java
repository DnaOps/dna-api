package dgu.edu.dnaapi.service;

import dgu.edu.dnaapi.domain.BoardSearchCriteria;
import dgu.edu.dnaapi.domain.Notices;
import dgu.edu.dnaapi.domain.dto.notices.NoticesDeleteRequestDto;
import dgu.edu.dnaapi.domain.dto.notices.NoticesResponseDto;
import dgu.edu.dnaapi.domain.dto.notices.NoticesUpdateRequestDto;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.domain.response.ListResponse;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.NoticesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
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
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + requestNoticeId, DnaStatusCode.INVALID_POST));
        // 게시글의 Author 만 게시글을 변경할 수 있도록 검사
        if (userId != notices.getAuthor().getId()){
            throw new DNACustomException("게시글의 작성자만 수정할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        notices.update(requestDto.getTitle(), requestDto.getContent());
        return requestNoticeId;
    }

    public Notices findById(Long id) {
        return noticesRepository.findById(id).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + id, DnaStatusCode.INVALID_POST));
    }

    @Transactional
    public Long delete(NoticesDeleteRequestDto requestDto, Long userId) {
        Long deleteId = requestDto.getNoticeId();
        Notices entity = noticesRepository.findById(deleteId).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + deleteId, DnaStatusCode.INVALID_POST));
        if(entity.getAuthor().getId() != userId) {
            throw new DNACustomException("해당 게시글의 작성자만 게시글을 삭제할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        noticesRepository.deleteById(deleteId);
        return deleteId;
    }

    public List<NoticesResponseDto> findAll() {
        List<Notices> noticesList = noticesRepository.findAll();
        return noticesList.stream().map(NoticesResponseDto::new).collect(Collectors.toList());
    }

    public ListResponse getAllNotices(Long start, Pageable pageable){
        Slice<Notices> noticesList;
        if(start == 0){
            noticesList = noticesRepository.findSliceBy(pageable);
        }else{
            noticesList = noticesRepository.findAllByNoticeId(start, pageable);
        }

        return ListResponse.builder()
                        .list(noticesList.getContent().stream().map(NoticesResponseDto::new))
                        .hasNext(noticesList.hasNext())
                        .build();
    }

    public ListResponse getAllNoticesByCriteria(Long start, BoardSearchCriteria criteria, String keyword, Pageable pageable){
        Slice<Notices> noticesList = null;

        if(criteria.equals(BoardSearchCriteria.TITLE)){
            if(start == 0){
                noticesList = noticesRepository.findAllByTitleContaining(keyword, pageable);
            }else{
                noticesList = noticesRepository.findAllByTitleContaining(start, keyword, pageable);
            }
        }else if (criteria.equals(BoardSearchCriteria.AUTHOR)){
            if(start == 0){
                noticesList = noticesRepository.findAllByAuthor_UserNameContaining(keyword, pageable);
            }else{
                noticesList = noticesRepository.findAllByAuthorContaining(start, keyword, pageable);
            }
        }else if (criteria.equals(BoardSearchCriteria.CONTENT)){
            if(start == 0){
                noticesList = noticesRepository.findAllByContentContaining(keyword, pageable);
            }else{
                noticesList = noticesRepository.findAllByContentContaining(start, keyword, pageable);
            }
        }else if (criteria.equals(BoardSearchCriteria.TITLE_AND_CONTENT)){
            if(start == 0){
                noticesList = noticesRepository.findAllByTitleContainingOrContentContaining(keyword, keyword, pageable);
            }else{
                noticesList = noticesRepository.findAllByTitleContainingOrContentContaining(start, keyword, pageable);
            }
        }else{
            throw new DNACustomException("Invalid BoardSearchCriteria option", DnaStatusCode.INVALID_SEARCH_OPTION);
        }

        if(noticesList == null){
            return ListResponse.builder()
                    .list(new ArrayList<>())
                    .hasNext(false)
                    .build();
        }

        return ListResponse.builder()
                .list(noticesList.getContent().stream().map(NoticesResponseDto::new))
                .hasNext(noticesList.hasNext())
                .build();
    }

}
