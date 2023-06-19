package dgu.edu.dnaapi.repository.albumPost;

import dgu.edu.dnaapi.domain.AlbumPostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AlbumPostCommentRepository extends JpaRepository<AlbumPostComment, Long>, AlbumPostCommentRepositoryCustom {

    @Query("SELECT c FROM AlbumPostComment c join fetch c.albumPost where c.albumPostCommentId = :albumPostCommentId")
    Optional<AlbumPostComment> findWithAlbumPostByAlbumPostCommentId(@Param("albumPostCommentId") Long albumPostCommentId);

    @Query("select c from AlbumPostComment c join fetch c.author join fetch c.albumPost where c.albumPostCommentId = :albumPostCommentId")
    Optional<AlbumPostComment> findWithAuthorAndAlbumPostByAlbumPostCommentId(@Param("albumPostCommentId") Long albumPostCommentId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE AlbumPostComment c set c.isDeleted = true where c.albumPostCommentId =:albumPostCommentId")
    void softDelete(@Param("albumPostCommentId") Long albumPostCommentId);

    @Modifying(clearAutomatically = true)
    @Query("update AlbumPostComment c set c.likeCount = c.likeCount + 1 where c.albumPostCommentId = :albumPostCommentId")
    int increaseLikeCount(@Param("albumPostCommentId") Long albumPostCommentId);

    @Modifying(clearAutomatically = true)
    @Query("update AlbumPostComment c set c.likeCount = c.likeCount - 1 where c.albumPostCommentId = :albumPostCommentId")
    int decreaseLikeCount(@Param("albumPostCommentId") Long albumPostCommentId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM AlbumPostComment c where c.albumPostCommentId = :albumPostCommentId")
    int deleteAlbumPostCommentByAlbumPostCommentId(@Param("albumPostCommentId") Long albumPostCommentId);
}
