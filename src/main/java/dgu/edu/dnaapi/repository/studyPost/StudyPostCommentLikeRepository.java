package dgu.edu.dnaapi.repository.studyPost;

import dgu.edu.dnaapi.domain.StudyPostCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudyPostCommentLikeRepository extends JpaRepository<StudyPostCommentLike, Long>, StudyPostCommentLikeRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM StudyPostCommentLike l WHERE l.studyPostComment.studyPostCommentId in (:studyPostCommentIds)")
    int deleteAllStudyPostCommentLikesInStudyPostCommentIds(@Param("studyPostCommentIds") List<Long> studyPostCommentIds);
}
