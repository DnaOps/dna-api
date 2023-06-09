package dgu.edu.dnaapi.repository.noticePost;

public interface NoticePostLikeRepositoryCustom {

    boolean findNoticePostLikeByNoticePostIdAndUserId(Long noticePostId, Long userId);
}
