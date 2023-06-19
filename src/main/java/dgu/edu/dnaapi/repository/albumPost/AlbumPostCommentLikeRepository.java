package dgu.edu.dnaapi.repository.albumPost;

import dgu.edu.dnaapi.domain.AlbumPostCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlbumPostCommentLikeRepository extends JpaRepository<AlbumPostCommentLike, Long>, AlbumPostCommentLikeRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM AlbumPostCommentLike l where l.albumPostComment.albumPostCommentId in (:albumPostCommentIds)")
    int deleteAllAlbumPostCommentLikesInAlbumPostCommentsIds(@Param("albumPostCommentIds") List<Long> albumPostCommentIds);
}
