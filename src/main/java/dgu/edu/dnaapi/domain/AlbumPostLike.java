package dgu.edu.dnaapi.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class AlbumPostLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long albumPostLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "albumPostId")
    private AlbumPost albumPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Builder
    public AlbumPostLike(AlbumPost albumPost, User user) {
        this.albumPost = albumPost;
        this.user = user;
    }
}
