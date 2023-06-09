package dgu.edu.dnaapi.service.ForumPost;

import dgu.edu.dnaapi.domain.*;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.forumPost.ForumPostLikeRepository;
import dgu.edu.dnaapi.repository.forumPost.ForumPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ForumPostLikeService {

    private final ForumPostRepository forumPostRepository;
    private final ForumPostLikeRepository forumPostLikeRepository;

    @Transactional
    public String like(User user, Long forumPostId) {
        ForumPost forumPost = getForumPost(forumPostId);
        forumPostLikeRepository.findByUserAndForumPost(user, forumPost).ifPresent(
                (forumPostLike) -> {
                    throw new DNACustomException("이미 해당 유저가 해당 게시글을 좋아합니다.", DnaStatusCode.INVALID_LIKE_OPERATION);
                }
        );

        forumPostRepository.increaseLikeCount(forumPost.getForumPostId());
        forumPostLikeRepository.save(new ForumPostLike(forumPost, user));
        return "added";
    }

    @Transactional
    public String dislike(User user, Long forumPostId) {
        ForumPost forumPost = getForumPost(forumPostId);
        ForumPostLike forumPostLike = forumPostLikeRepository.findByUserAndForumPost(user, forumPost).orElseThrow(
                () -> new DNACustomException("현재 해당 유저가 해당 게시글을 좋아하지 않는 상태입니다.", DnaStatusCode.INVALID_LIKE_OPERATION));

        forumPostRepository.decreaseLikeCount(forumPost.getForumPostId());
        forumPostLikeRepository.delete(forumPostLike);
        return "deleted";
    }

    private ForumPost getForumPost(Long forumPostId) {
        ForumPost forumPost = forumPostRepository.findById(forumPostId).orElseThrow(
                () -> new DNACustomException("해당 ForumPost를 찾을 수 없습니다. id = " + forumPostId, DnaStatusCode.INVALID_POST));
        return forumPost;
    }

    public boolean isForumLikedByUser(User user, ForumPost forumPost) {
        return forumPostLikeRepository.findByUserAndForumPost(user, forumPost) != null;
    }
}
