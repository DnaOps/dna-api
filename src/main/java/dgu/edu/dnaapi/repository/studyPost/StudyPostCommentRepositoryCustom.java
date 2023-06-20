package dgu.edu.dnaapi.repository.studyPost;

import dgu.edu.dnaapi.domain.StudyPostComment;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudyPostCommentRepositoryCustom {

    List<StudyPostComment> search(Long studyPostId, Long start, Pageable pageable);
    List<StudyPostComment> findAllReplyStudyPostComments(Long studyPostId, List<Long> commentIds);
    List<StudyPostComment> findAllReplyStudyPostCommentsByCommentGroupId(Long studyPostId, Long commentGroupId);
    List<StudyPostComment> findAllStudyPostCommentsWithStudyPostByStudyPostId(Long studyPostId);
}
