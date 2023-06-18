package dgu.edu.dnaapi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class AlbumPostImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long albumPostImageId;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "albumPostId")
    private AlbumPost albumPost;

    public AlbumPostImage(String imageUrl, AlbumPost albumPost) {
        this.imageUrl = imageUrl;
        this.albumPost = albumPost;
    }

    public void setAlbumPost(AlbumPost albumPost) {
        this.albumPost = albumPost;
    }
}
