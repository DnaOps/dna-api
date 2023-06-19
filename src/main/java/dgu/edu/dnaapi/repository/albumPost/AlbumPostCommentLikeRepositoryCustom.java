package dgu.edu.dnaapi.repository.albumPost;

import dgu.edu.dnaapi.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AlbumPostCommentLikeRepositoryCustom {

    Set<Long> findAlbumPostCommentIdsLikedByUserId(Long userId, List<Long> allAlbumCommentIds);
    boolean isAlbumPostLikedByUser(User user, AlbumPostComment albumPostComment);
    Optional<AlbumPostCommentLike> findAlbumPostCommentLikeByUserIdAndAlbumPostCommentId(Long userId, Long albumPostCommentId);
    long deleteAllAlbumPostCommentLikesByAlbumPostCommentId(Long albumPostCommentId);
}
