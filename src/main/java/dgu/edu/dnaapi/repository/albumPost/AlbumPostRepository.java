package dgu.edu.dnaapi.repository.albumPost;

import dgu.edu.dnaapi.domain.AlbumPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlbumPostRepository extends JpaRepository<AlbumPost, Long>, AlbumPostRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE AlbumPost a set a.likeCount = a.likeCount + 1 where a.albumPostId = :albumPostId")
    int increaseLikeCount(@Param("albumPostId") Long albumPostId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE AlbumPost a set a.likeCount = a.likeCount - 1 where a.albumPostId = :albumPostId")
    int decreaseLikeCount(@Param("albumPostId") Long albumPostId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE AlbumPost a set a.commentCount = a.commentCount + 1 where a.albumPostId = :albumPostId")
    int increaseCommentCount(@Param("albumPostId") Long albumPostId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE AlbumPost a set a.commentCount = a.commentCount - 1 where a.albumPostId = :albumPostId")
    int decreaseCommentCount(@Param("albumPostId") Long albumPostId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM AlbumPost a WHERE a.albumPostId = :albumPostId")
    int deleteAlbumPostByAlbumPostId(@Param("albumPostId") Long albumPostId);
}
