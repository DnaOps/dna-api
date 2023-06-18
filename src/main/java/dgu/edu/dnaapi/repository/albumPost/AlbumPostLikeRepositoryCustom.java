package dgu.edu.dnaapi.repository.albumPost;

public interface AlbumPostLikeRepositoryCustom {

    boolean findAlbumPostLikeByAlbumPostIdAndUserId(Long albumPostId, Long userId);
}
