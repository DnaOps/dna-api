package dgu.edu.dnaapi.repository.studyPost;

import dgu.edu.dnaapi.domain.StudyPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudyPostRepository extends JpaRepository<StudyPost, Long>, StudyPostRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE StudyPost s set s.likeCount = s.likeCount + 1 where s.studyPostId = :studyPostId")
    int increaseLikeCount(@Param("studyPostId")Long studyPostId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE StudyPost s set s.likeCount = s.likeCount - 1 where s.studyPostId = :studyPostId")
    int decreaseLikeCount(@Param("studyPostId")Long studyPostId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE StudyPost s set s.commentCount = s.commentCount + 1 where s.studyPostId = :studyPostId")
    int increaseCommentCount(@Param("studyPostId")Long studyPostId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE StudyPost s set s.commentCount = s.commentCount - 1 where s.studyPostId = :studyPostId")
    int decreaseCommentCount(@Param("studyPostId")Long studyPostId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM StudyPost s WHERE s.studyPostId = :studyPostId")
    int deleteStudyPostByStudyPostId(@Param("studyPostId") Long studyPostId);
}
