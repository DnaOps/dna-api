package dgu.edu.dnaapi.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.util.StringUtils.hasText;

@Entity
@Getter
@NoArgsConstructor
@Builder
public class AlbumPost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long albumPostId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User author;

    @OneToMany(mappedBy = "albumPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlbumPostComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "albumPost", cascade = CascadeType.ALL)
    private Set<AlbumPostLike> likes = new HashSet<>();

    @Column(columnDefinition = "int default 0", nullable = false)
    @Builder.Default
    private int likeCount = 0;

    @Column(columnDefinition = "int default 0", nullable = false)
    @Builder.Default
    private int commentCount = 0;

    @OneToMany(mappedBy = "albumPost", cascade = CascadeType.ALL)
    private List<AlbumPostImage> albumPostImages = new ArrayList<>();

    private String thumbnailImage;

    public AlbumPost(Long albumPostId, String title, String content, User author, List<AlbumPostComment> comments, Set<AlbumPostLike> likes, int likeCount, int commentCount, List<AlbumPostImage> albumPostImages, String thumbnailImage) {
        this.albumPostId = albumPostId;
        this.title = title;
        this.content = content;
        this.author = author;
        this.comments = comments;
        this.likes = likes;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.albumPostImages = albumPostImages;
        this.thumbnailImage = thumbnailImage;
    }

    public void update(String title, String content, String thumbnailImage) {
        if(hasText(title))
            this.title = title;
        if(hasText(content))
            this.content = content;
        if(hasText(thumbnailImage))
            this.thumbnailImage = thumbnailImage;
    }

    public void addAlbumPostImages(List<AlbumPostImage> albumPostImages) {
        this.albumPostImages.addAll(albumPostImages);
        for (AlbumPostImage albumPostImage : albumPostImages) {
            albumPostImage.setAlbumPost(this);
        }
    }

    public void setThumbnailImage(String thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }
}
