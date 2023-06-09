package dgu.edu.dnaapi.repository.noticePost;

import dgu.edu.dnaapi.domain.NoticePostCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticePostCommentLikeRepository extends JpaRepository<NoticePostCommentLike, Long>, NoticePostCommentLikeRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM NoticePostCommentLike l WHERE l.noticePostComment.noticePostCommentId in (:noticePostCommentIds)")
    int deleteAllNoticePostCommentLikesInNoticePostCommentIds(@Param("noticePostCommentIds") List<Long> noticePostCommentIds);
}
