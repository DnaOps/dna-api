package dgu.edu.dnaapi.repository.noticePost;

import dgu.edu.dnaapi.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface NoticePostCommentLikeRepositoryCustom {

    Optional<NoticePostCommentLike> findWithNoticePostCommentByUserIdAndNoticePostCommentId(Long userId, Long noticePostCommentId);
    long deleteAllNoticePostCommentLikesByNoticePostCommentId(Long noticePostCommentId);
    Set<Long> findNoticePostCommentIdsLikedByUserId(Long userId, List<Long> noticePostCommentIds);
}
