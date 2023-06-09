package dgu.edu.dnaapi.repository.noticePost;

import dgu.edu.dnaapi.domain.NoticePostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NoticePostCommentRepository extends JpaRepository<NoticePostComment, Long>, NoticePostCommentRepositoryCustom {

    @Query("SELECT c FROM NoticePostComment c join fetch c.noticePost WHERE c.noticePostCommentId = :noticePostCommentId")
    Optional<NoticePostComment> findWithNoticePostByNoticePostCommentId(@Param("noticePostCommentId") Long noticePostCommentId);

    @Query("SELECT c FROM NoticePostComment c join fetch c.author join fetch c.noticePost WHERE c.noticePostCommentId = :noticePostCommentId")
    Optional<NoticePostComment> findWithAuthorAndNoticePostByNoticePostCommentId(@Param("noticePostCommentId") Long noticePostCommentId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE NoticePostComment c set c.isDeleted = true WHERE c.noticePostCommentId = :noticePostCommentId")
    void softDelete(@Param("noticePostCommentId") Long noticePostCommentId);

    @Modifying(clearAutomatically = true)
    @Query("update NoticePostComment c set c.likeCount = c.likeCount + 1 where c.noticePostCommentId = :noticePostCommentId")
    int increaseLikeCount(@Param("noticePostCommentId") Long noticePostCommentId);

    @Modifying(clearAutomatically = true)
    @Query("update NoticePostComment c set c.likeCount = c.likeCount - 1 where c.noticePostCommentId = :noticePostCommentId")
    int decreaseLikeCount(@Param("noticePostCommentId") Long noticePostCommentId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM NoticePostComment c where c.noticePostCommentId = :noticePostCommentId")
    int deleteNoticePostCommentByNoticePostCommentId(@Param("noticePostCommentId") Long noticePostCommentId);
}
