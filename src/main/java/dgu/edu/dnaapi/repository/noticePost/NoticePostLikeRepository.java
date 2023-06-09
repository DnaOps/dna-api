package dgu.edu.dnaapi.repository.noticePost;

import dgu.edu.dnaapi.domain.NoticePostLike;
import dgu.edu.dnaapi.domain.NoticePost;
import dgu.edu.dnaapi.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NoticePostLikeRepository extends JpaRepository<NoticePostLike, Long>, NoticePostLikeRepositoryCustom {
    Optional<NoticePostLike> findByUserAndNoticePost(User user, NoticePost noticePost);

    @Modifying(clearAutomatically = true)
    @Query("delete from NoticePostLike n where n.noticePost.noticePostId = :noticePostId")
    int deleteAllNoticePostLikesByNoticePostId(@Param("noticePostId") Long noticePostId);
}