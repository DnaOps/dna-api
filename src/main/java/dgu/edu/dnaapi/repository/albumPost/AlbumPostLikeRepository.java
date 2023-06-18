package dgu.edu.dnaapi.repository.albumPost;

import dgu.edu.dnaapi.domain.AlbumPost;
import dgu.edu.dnaapi.domain.AlbumPostLike;
import dgu.edu.dnaapi.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AlbumPostLikeRepository extends JpaRepository<AlbumPostLike, Long>, AlbumPostLikeRepositoryCustom {

    Optional<AlbumPostLike> findByUserAndAlbumPost(User user, AlbumPost albumPost);

    @Modifying(clearAutomatically = true)
    @Query("delete from AlbumPostLike a where a.albumPost.albumPostId = :albumPostId")
    int deleteAllAlbumPostLikesByAlbumPostId(@Param("albumPostId")Long albumPostId);
}
