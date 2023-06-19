package dgu.edu.dnaapi.repository.albumPost;

import dgu.edu.dnaapi.controller.dto.PostSearchCondition;
import dgu.edu.dnaapi.domain.AlbumPost;
import dgu.edu.dnaapi.domain.dto.albumPost.AlbumPostMetaDataResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AlbumPostRepositoryCustom {

    Optional<AlbumPost> findWithAlbumPostImagesByAlbumPostId(Long albumPostId);
    List<AlbumPostMetaDataResponseDto> search(PostSearchCondition condition, Pageable pageable);
    Optional<AlbumPost> findWithAlbumPostCommentsByAlbumPostId(Long albumPostId);
}
