package dgu.edu.dnaapi.repository.albumPost;

import dgu.edu.dnaapi.domain.AlbumPostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlbumPostImageRepository extends JpaRepository<AlbumPostImage, Long> {

    @Query("SELECT i FROM AlbumPostImage i WHERE i.albumPost.albumPostId = :albumPostId")
    List<AlbumPostImage> findAllAlbumPostImageByAlbumPostId(@Param("albumPostId") Long albumPostId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM AlbumPostImage i WHERE i.albumPost.albumPostId = :albumPostId")
    int deleteAlbumPostImagesByAlbumPostId(@Param("albumPostId") Long albumPostId);

    @Modifying
    @Query("DELETE FROM AlbumPostImage i WHERE i.albumPostImageId = :albumPostImageId")
    int deleteByAlbumPostImageId(@Param("albumPostImageId") Long albumPostImageId);
}
