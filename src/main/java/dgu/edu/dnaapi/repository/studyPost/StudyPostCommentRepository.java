package dgu.edu.dnaapi.repository.studyPost;

import dgu.edu.dnaapi.domain.StudyPostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudyPostCommentRepository extends JpaRepository<StudyPostComment, Long>, StudyPostCommentRepositoryCustom {

    @Query("SELECT c FROM StudyPostComment c join fetch c.studyPost WHERE c.studyPostCommentId = :studyPostCommentId")
    Optional<StudyPostComment> findWithStudyPostByStudyPostCommentId(@Param("studyPostCommentId") Long studyPostCommentId);

    @Query("SELECT c FROM StudyPostComment c join fetch c.author join fetch c.studyPost WHERE c.studyPostCommentId = :studyPostCommentId")
    Optional<StudyPostComment> findWithAuthorAndStudyPostByStudyPostCommentId(@Param("studyPostCommentId") Long studyPostCommentId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE StudyPostComment c set c.isDeleted = true WHERE c.studyPostCommentId = :studyPostCommentId")
    void softDelete(@Param("studyPostCommentId") Long studyPostCommentId);

    @Modifying(clearAutomatically = true)
    @Query("update StudyPostComment c set c.likeCount = c.likeCount + 1 where c.studyPostCommentId = :studyPostCommentId")
    int increaseLikeCount(@Param("studyPostCommentId") Long studyPostCommentId);

    @Modifying(clearAutomatically = true)
    @Query("update StudyPostComment c set c.likeCount = c.likeCount - 1 where c.studyPostCommentId = :studyPostCommentId")
    int decreaseLikeCount(@Param("studyPostCommentId") Long studyPostCommentId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM StudyPostComment c where c.studyPostCommentId = :studyPostCommentId")
    int deleteStudyPostCommentByStudyPostCommentId(@Param("studyPostCommentId") Long studyPostCommentId);
}
