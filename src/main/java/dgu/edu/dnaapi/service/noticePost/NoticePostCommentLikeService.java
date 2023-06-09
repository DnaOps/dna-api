package dgu.edu.dnaapi.service.noticePost;

import dgu.edu.dnaapi.domain.*;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.noticePost.NoticePostCommentLikeRepository;
import dgu.edu.dnaapi.repository.noticePost.NoticePostCommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class NoticePostCommentLikeService {

    private final NoticePostCommentLikeRepository noticePostCommentLikeRepository;
    private final NoticePostCommentRepository noticePostCommentRepository;

    @Transactional
    public String like(User user, Long noticePostId, Long noticePostCommentId) {
        NoticePostComment noticePostComment = getNoticePostComment(noticePostCommentId);
        checkNoticePostCommentIsInNoticePost(noticePostId, noticePostComment);

        noticePostCommentLikeRepository.findWithNoticePostCommentByUserIdAndNoticePostCommentId(user.getId(), noticePostCommentId).ifPresent(
                (noticePostCommentLike) -> {
                    throw new DNACustomException("이미 해당 유저가 해당 댓글을 좋아합니다.", DnaStatusCode.INVALID_LIKE_OPERATION);
                }
        );
        return likeNoticeComment(user, noticePostComment);
    }

    @Transactional
    public String dislike(User user, Long noticePostId, Long noticePostCommentId) {
        NoticePostComment noticePostComment = getNoticePostComment(noticePostCommentId);
        checkNoticePostCommentIsInNoticePost(noticePostId, noticePostComment);

        NoticePostCommentLike noticePostCommentLike = noticePostCommentLikeRepository.findWithNoticePostCommentByUserIdAndNoticePostCommentId(user.getId(), noticePostCommentId).orElseThrow(
                () -> new DNACustomException("현재 해당 유저가 해당 댓글을 좋아하지 않습니다.", DnaStatusCode.INVALID_LIKE_OPERATION));

        return disLikeNoticeComment(noticePostCommentLike);
    }

    private NoticePostComment getNoticePostComment(Long noticePostCommentId) {
        NoticePostComment noticePostComment =  noticePostCommentRepository.findWithNoticePostByNoticePostCommentId(noticePostCommentId).orElseThrow(
                () -> new DNACustomException("해당 댓글이 존재하지 않습니다. id = " + noticePostCommentId, DnaStatusCode.INVALID_COMMENT));
        if(noticePostComment.isDeleted())
            throw new DNACustomException("해당 댓글이 존재하지 않습니다. id = " + noticePostCommentId, DnaStatusCode.INVALID_DELETE_COMMENT);
        return noticePostComment;
    }

    private void checkNoticePostCommentIsInNoticePost(Long noticePostId, NoticePostComment noticePostComment) {
        if(!noticePostComment.getNoticePost().getNoticePostId().equals(noticePostId))
            throw new DNACustomException("해당 게시글에 해당 댓글이 존재하지 않습니다. noticePostId = " + noticePostId + "commentId = " + noticePostComment.getNoticePostCommentId(), DnaStatusCode.INVALID_COMMENT);
    }

    private String disLikeNoticeComment(NoticePostCommentLike noticePostCommentLike) {
        noticePostCommentRepository.decreaseLikeCount(noticePostCommentLike.getNoticePostComment().getNoticePostCommentId());
        noticePostCommentLikeRepository.deleteById(noticePostCommentLike.getNoticePostCommentLikeId());
        return "deleted";
    }

    private String likeNoticeComment(User user, NoticePostComment noticePostComment) {
        noticePostCommentRepository.increaseLikeCount(noticePostComment.getNoticePostCommentId());
        noticePostCommentLikeRepository.save(new NoticePostCommentLike(noticePostComment, user));
        return "added";
    }
}
