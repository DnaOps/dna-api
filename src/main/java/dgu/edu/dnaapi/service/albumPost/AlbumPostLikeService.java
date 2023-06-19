package dgu.edu.dnaapi.service.albumPost;

import dgu.edu.dnaapi.domain.*;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.albumPost.AlbumPostLikeRepository;
import dgu.edu.dnaapi.repository.albumPost.AlbumPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlbumPostLikeService {

    private final AlbumPostRepository albumPostRepository;
    private final AlbumPostLikeRepository albumPostLikeRepository;

    @Transactional
    public String like(User user, Long albumPostId) {
        AlbumPost albumPost = getAlbumPost(albumPostId);
        albumPostLikeRepository.findByUserAndAlbumPost(user, albumPost).ifPresent(
                (forumPostLike) -> {
                    throw new DNACustomException("이미 해당 유저가 해당 게시글을 좋아합니다.", DnaStatusCode.INVALID_LIKE_OPERATION);
                }
        );

        albumPostRepository.increaseLikeCount(albumPost.getAlbumPostId());
        albumPostLikeRepository.save(new AlbumPostLike(albumPost, user));
        return "added";
    }

    @Transactional
    public String dislike(User user, Long albumPostId) {
        AlbumPost albumPost = getAlbumPost(albumPostId);
        AlbumPostLike albumPostLike = albumPostLikeRepository.findByUserAndAlbumPost(user, albumPost).orElseThrow(
                () -> new DNACustomException("현재 해당 유저가 해당 게시글을 좋아하지 않는 상태입니다.", DnaStatusCode.INVALID_LIKE_OPERATION));

        albumPostRepository.decreaseLikeCount(albumPost.getAlbumPostId());
        albumPostLikeRepository.delete(albumPostLike);
        return "deleted";
    }

    private AlbumPost getAlbumPost(Long albumPostId) {
        AlbumPost albumPost = albumPostRepository.findById(albumPostId).orElseThrow(
                () -> new DNACustomException("해당 ForumPost를 찾을 수 없습니다. id = " + albumPostId, DnaStatusCode.INVALID_POST));
        return albumPost;
    }
}
