package dgu.edu.dnaapi.service.noticePost;

import dgu.edu.dnaapi.domain.*;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.noticePost.NoticePostLikeRepository;
import dgu.edu.dnaapi.repository.noticePost.NoticePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticePostLikeService {
    private final NoticePostLikeRepository noticePostLikeRepository;
    private final NoticePostRepository noticePostRepository;

    @Transactional
    public String like(User user, Long noticePostId) {
        NoticePost noticePost = getNoticePost(noticePostId);
        noticePostLikeRepository.findByUserAndNoticePost(user, noticePost).ifPresent(
                (noticePostLike) -> {
                    throw new DNACustomException("이미 해당 유저가 해당 게시글을 좋아합니다.", DnaStatusCode.INVALID_LIKE_OPERATION);
                }
        );
        noticePostRepository.increaseLikeCount(noticePost.getNoticePostId());
        noticePostLikeRepository.save(new NoticePostLike(noticePost, user));
        return "added";
    }

    @Transactional
    public String dislike(User user, Long noticePostId) {
        NoticePost noticePost = getNoticePost(noticePostId);
        NoticePostLike noticePostLike = noticePostLikeRepository.findByUserAndNoticePost(user, noticePost).orElseThrow(
                () -> new DNACustomException("현재 해당 유저가 해당 게시글을 좋아하지 않는 상태입니다.", DnaStatusCode.INVALID_LIKE_OPERATION));

        noticePostRepository.decreaseLikeCount(noticePost.getNoticePostId());
        noticePostLikeRepository.delete(noticePostLike);
        return "deleted";
    }

    private NoticePost getNoticePost(Long noticePostId) {
        return noticePostRepository.findById(noticePostId).orElseThrow(
                () -> new DNACustomException("해당 ForumPost를 찾을 수 없습니다. id = " + noticePostId, DnaStatusCode.INVALID_POST));
    }
}
