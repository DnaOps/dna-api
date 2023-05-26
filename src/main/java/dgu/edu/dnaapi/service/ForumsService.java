package dgu.edu.dnaapi.service;

import dgu.edu.dnaapi.controller.dto.BoardSearchCondition;
import dgu.edu.dnaapi.domain.Forums;
import dgu.edu.dnaapi.domain.dto.forum.ForumSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.forum.ForumsMetaDataResponseDto;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.domain.response.ListResponse;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.forum.ForumsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ForumsService {

    private final ForumsRepository forumsRepository;

    @Transactional
    public Long save(Forums forums) {
        return forumsRepository.save(forums).getForumId();
    }

    @Transactional
    public Long update(ForumSaveRequestDto requestDto, Long userId, Long forumId) {

        Forums forum = forumsRepository.findById(forumId).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + forumId, DnaStatusCode.INVALID_POST));
        // 게시글의 Author 만 게시글을 변경할 수 있도록 검사
        if (userId != forum.getAuthor().getId()){
            throw new DNACustomException("게시글의 작성자만 수정할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        forum.update(requestDto.getTitle(), requestDto.getContent());
        return forum.getForumId();
    }

    public Forums findById(Long id) {
        return forumsRepository.findById(id).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + id, DnaStatusCode.INVALID_POST));
    }

    @Transactional
    public Long delete(Long deleteForumId, Long userId) {
        Forums entity = forumsRepository.findById(deleteForumId).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + deleteForumId, DnaStatusCode.INVALID_POST));
        if(entity.getAuthor().getId() != userId) {
            throw new DNACustomException("해당 게시글의 작성자만 게시글을 삭제할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        forumsRepository.deleteById(deleteForumId);
        return deleteForumId;
    }

    public List<ForumsMetaDataResponseDto> findAllForumMetaData() {
        List<Forums> forums = forumsRepository.findAll();
        return forums.stream().map(ForumsMetaDataResponseDto::new).collect(Collectors.toList());
    }

    public ListResponse findAllForumsMetaDataWithCondition(BoardSearchCondition condition, Pageable pageable) {
        List<ForumsMetaDataResponseDto> forums = forumsRepository.search(condition, pageable);
        boolean hasNext = false;
        if(forums.size() > pageable.getPageSize()){
            forums.remove(pageable.getPageSize());
            hasNext = true;
        }
        return ListResponse.builder()
                        .list(forums)
                        .hasNext(hasNext)
                        .build();
    }
}
