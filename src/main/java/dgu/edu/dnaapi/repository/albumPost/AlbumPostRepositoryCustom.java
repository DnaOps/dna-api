package dgu.edu.dnaapi.repository.albumPost;

import dgu.edu.dnaapi.domain.AlbumPost;

import java.util.Optional;

public interface AlbumPostRepositoryCustom {

    Optional<AlbumPost> findWithAlbumPostImagesByAlbumPostId(Long albumPostId);
}
