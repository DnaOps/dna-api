package dgu.edu.dnaapi.repository.studyPost;

import dgu.edu.dnaapi.domain.StudyPostCommentLike;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StudyPostCommentLikeRepositoryCustom {

    Optional<StudyPostCommentLike> findWithStudyPostCommentByUserIdAndStudyPostCommentId(Long userId, Long studyPostCommentId);
    long deleteAllStudyPostCommentLikesByStudyPostCommentId(Long studyPostCommentId);
    Set<Long> findStudyPostCommentIdsLikedByUserId(Long userId, List<Long> studyPostCommentIds);
}
