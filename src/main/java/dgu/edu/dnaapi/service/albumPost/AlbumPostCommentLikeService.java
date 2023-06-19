package dgu.edu.dnaapi.service.albumPost;

import dgu.edu.dnaapi.domain.*;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.albumPost.AlbumPostCommentLikeRepository;
import dgu.edu.dnaapi.repository.albumPost.AlbumPostCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlbumPostCommentLikeService {

    private final AlbumPostCommentLikeRepository albumPostCommentLikeRepository;
    private final AlbumPostCommentRepository albumPostCommentRepository;

    @Transactional
    public String like(User user, Long albumPostId, Long albumPostCommentId) {
        AlbumPostComment albumPostComment = getAlbumPostComment(albumPostCommentId);
        checkAlbumPostCommentIsInAlbumPost(albumPostId, albumPostCommentId, albumPostComment);

        albumPostCommentLikeRepository.findAlbumPostCommentLikeByUserIdAndAlbumPostCommentId(user.getId(), albumPostCommentId).ifPresent(
                (albumPostCommentLike) -> {
                    throw new DNACustomException("이미 해당 유저가 해당 댓글을 좋아합니다.", DnaStatusCode.INVALID_LIKE_OPERATION);
                }
        );
        return likeAlbumPostComment(user, albumPostComment);
    }

    @Transactional
    public String dislike(User user, Long albumPostId, Long albumPostCommentId) {
        AlbumPostComment albumPostComment = getAlbumPostComment(albumPostCommentId);
        checkAlbumPostCommentIsInAlbumPost(albumPostId, albumPostCommentId, albumPostComment);

        AlbumPostCommentLike albumPostCommentLike = albumPostCommentLikeRepository.findAlbumPostCommentLikeByUserIdAndAlbumPostCommentId(user.getId(), albumPostCommentId).orElseThrow(
                () -> new DNACustomException("현재 해당 유저가 해당 댓글을 좋아하지 않습니다.", DnaStatusCode.INVALID_LIKE_OPERATION));

        return disLikeAlbumPostComment(albumPostCommentLike);
    }

    private AlbumPostComment getAlbumPostComment(Long albumPostCommentId) {
        AlbumPostComment albumPostComment = albumPostCommentRepository.findWithAlbumPostByAlbumPostCommentId(albumPostCommentId).orElseThrow(
                () -> new DNACustomException("해당 댓글이 존재하지 않습니다. id = " + albumPostCommentId, DnaStatusCode.INVALID_COMMENT));
        if(albumPostComment.getIsDeleted())
            throw new DNACustomException("해당 댓글이 존재하지 않습니다. id = " + albumPostCommentId, DnaStatusCode.INVALID_DELETE_COMMENT);
        return albumPostComment;
    }

    private void checkAlbumPostCommentIsInAlbumPost(Long albumPostId, Long albumPostCommentId, AlbumPostComment albumPostComment) {
        if(!albumPostComment.getAlbumPost().getAlbumPostId().equals(albumPostId))
            throw new DNACustomException("해당 게시글에 해당 댓글이 존재하지 않습니다. forumPostId = " + albumPostId + "commentId = " + albumPostCommentId, DnaStatusCode.INVALID_COMMENT);
    }

    private String disLikeAlbumPostComment(AlbumPostCommentLike albumPostCommentLike) {
        albumPostCommentRepository.decreaseLikeCount(albumPostCommentLike.getAlbumPostComment().getAlbumPostCommentId());
        albumPostCommentLikeRepository.delete(albumPostCommentLike);
        return "deleted";
    }

    private String likeAlbumPostComment(User user, AlbumPostComment albumPostComment) {
        albumPostCommentRepository.increaseLikeCount(albumPostComment.getAlbumPostCommentId());
        albumPostCommentLikeRepository.save(new AlbumPostCommentLike(albumPostComment, user));
        return "added";
    }
}
