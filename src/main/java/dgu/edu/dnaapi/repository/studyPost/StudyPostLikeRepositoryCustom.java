package dgu.edu.dnaapi.repository.studyPost;

public interface StudyPostLikeRepositoryCustom {

    boolean findStudyPostLikeByStudyPostIdAndUserId(Long studyPostId, Long userId);
}
