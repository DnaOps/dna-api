package dgu.edu.dnaapi.repository.albumPost;

import dgu.edu.dnaapi.domain.AlbumPostComment;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AlbumPostCommentRepositoryCustom {

    List<AlbumPostComment> search(Long albumPostId, Long start, Pageable pageable);
    List<AlbumPostComment> findAllReplyAlbumPostComments(Long albumPostId, List<Long> commentsId);
    List<AlbumPostComment> findAllReplyAlbumPostCommentsByCommentGroupId(Long albumPostId, Long commentGroupId);
    List<AlbumPostComment> findAllAlbumPostCommentsByAlbumPostId(Long albumPostId);
    Long getAlbumPostCommentCountByUserId(Long userId);
}
