package dgu.edu.dnaapi.repository.noticePost;

import dgu.edu.dnaapi.domain.NoticePostComment;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NoticePostCommentRepositoryCustom {

    List<NoticePostComment> search(Long noticePostId, Long start, Pageable pageable);
    List<NoticePostComment> findAllReplyNoticePostComments(Long noticePostId, List<Long> commentIds);
    List<NoticePostComment> findAllReplyNoticePostCommentsByCommentGroupId(Long noticePostId, Long commentGroupId);
    List<NoticePostComment> findAllNoticePostCommentsWithNoticePostByNoticePostId(Long noticePostId);
}
