package dgu.edu.dnaapi.repository.studyPost;

import dgu.edu.dnaapi.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudyPostLikeRepository extends JpaRepository<StudyPostLike, Long>, StudyPostLikeRepositoryCustom {

    Optional<StudyPostLike> findByUserAndStudyPost(User user, StudyPost studyPost);

    @Modifying(clearAutomatically = true)
    @Query("delete from StudyPostLike l where l.studyPost.studyPostId = :studyPostId")
    int deleteAllStudyPostLikesByStudyPostId(@Param("studyPostId") Long studyPostId);
}
