package dgu.edu.dnaapi.repository.studyPost;

import dgu.edu.dnaapi.controller.dto.PostSearchCondition;
import dgu.edu.dnaapi.domain.StudyPost;
import dgu.edu.dnaapi.domain.dto.studyPost.StudyPostMetaDataResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface StudyPostRepositoryCustom {

    List<StudyPostMetaDataResponseDto> search(PostSearchCondition condition, Pageable pageable);
    Optional<StudyPost> findWithStudyPostCommentsByStudyPostId(Long studyPostId);
    Optional<StudyPost> findWithAuthorByStudyPostId(Long studyPostId);
    Long getStudyPostCountByUserId(Long userId);
}
