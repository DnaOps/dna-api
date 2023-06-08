package dgu.edu.dnaapi.service.notice;

import dgu.edu.dnaapi.controller.dto.BoardSearchCondition;
import dgu.edu.dnaapi.domain.BoardSearchCriteria;
import dgu.edu.dnaapi.domain.Notices;
import dgu.edu.dnaapi.domain.dto.forum.ForumsMetaDataResponseDto;
import dgu.edu.dnaapi.domain.dto.notices.NoticesDeleteRequestDto;
import dgu.edu.dnaapi.domain.dto.notices.NoticesMetaDataResponseDto;
import dgu.edu.dnaapi.domain.dto.notices.NoticesResponseDto;
import dgu.edu.dnaapi.domain.dto.notices.NoticesUpdateRequestDto;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.domain.response.ListResponse;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.notice.NoticesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class NoticesService {
    private final NoticesRepository noticesRepository;

    @Transactional
    public Long save(Notices notices) {
        if(!hasText(notices.getTitle()) || !hasText(notices.getContent()))
            throw new DNACustomException("제목이나 내용이 없는 글입니다. 게시글 작성 조건을 확인해주세요.", DnaStatusCode.INVALID_INPUT);
        return noticesRepository.save(notices).getNoticeId();
    }

    @Transactional
    public Long update(NoticesUpdateRequestDto requestDto,Long updateNoticeId, Long userId) {
        Notices notice = noticesRepository.findById(updateNoticeId).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + updateNoticeId, DnaStatusCode.INVALID_POST));
        if (userId != notice.getAuthor().getId()){
            throw new DNACustomException("게시글의 작성자만 수정할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        notice.update(requestDto.getTitle(), requestDto.getContent());
        return updateNoticeId;
    }

    public Notices findById(Long id) {
        return noticesRepository.findById(id).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + id, DnaStatusCode.INVALID_POST));
    }

    @Transactional
    public Long delete(Long deletedNoticeId, Long userId) {
        Notices entity = noticesRepository.findById(deletedNoticeId).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + deletedNoticeId, DnaStatusCode.INVALID_POST));
        if(entity.getAuthor().getId() != userId) {
            throw new DNACustomException("해당 게시글의 작성자만 게시글을 삭제할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        noticesRepository.deleteById(deletedNoticeId);
        return deletedNoticeId;
    }

    public ListResponse findAllNoticesMetaDataWithCondition(BoardSearchCondition condition, Pageable pageable) {
        List<NoticesMetaDataResponseDto> noticesMetaDataResponseDtoList = noticesRepository.search(condition, pageable);
        boolean hasNext = false;
        if(noticesMetaDataResponseDtoList.size() > pageable.getPageSize()){
            noticesMetaDataResponseDtoList.remove(pageable.getPageSize());
            hasNext = true;
        }
        return ListResponse.builder()
                .list(noticesMetaDataResponseDtoList)
                .hasNext(hasNext)
                .build();
    }

    // Todo : NoticeLiked 확인 Query 작성
    public NoticesResponseDto findNoticeWithLikedInfoByNoticeIdAndUserId(Long noticeId, Long userId) {
        Notices notice = noticesRepository.findById(noticeId).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + noticeId, DnaStatusCode.INVALID_POST));
        return new NoticesResponseDto(notice, false);
    }

    /*
    * Legacy Code (JPQL)
    * */

    public List<NoticesMetaDataResponseDto> findAll() {
        List<Notices> noticesList = noticesRepository.findAll();
        return noticesList.stream().map(NoticesMetaDataResponseDto::new).collect(Collectors.toList());
    }

    public ListResponse getAllNotices(Long start, Pageable pageable){
        Slice<Notices> noticesList;
        if(start == 0){
            noticesList = noticesRepository.findSliceBy(pageable);
        }else{
            noticesList = noticesRepository.findAllByNoticeId(start, pageable);
        }

        return ListResponse.builder()
                .list(noticesList.getContent().stream().map(NoticesMetaDataResponseDto::new))
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
                .list(noticesList.getContent().stream().map(NoticesMetaDataResponseDto::new))
                .hasNext(noticesList.hasNext())
                .build();
    }

}
