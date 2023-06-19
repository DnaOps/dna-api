package dgu.edu.dnaapi.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Entity
@Getter
@NoArgsConstructor
@Builder
public class AlbumPostComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long albumPostCommentId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId")
    private AlbumPostComment parent;

    private Long commentGroupId;

    @OneToMany(mappedBy = "parent")
    private List<AlbumPostComment> childList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "albumPostId", nullable = false)
    private AlbumPost albumPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User author;

    @Column(columnDefinition = "int default 0", nullable = false)
    @Builder.Default
    private int likeCount = 0;

    private Boolean isDeleted = false;

    public AlbumPostComment(Long albumPostCommentId, String content, AlbumPostComment parent, Long commentGroupId, List<AlbumPostComment> childList, AlbumPost albumPost, User author, int likeCount, Boolean isDeleted) {
        this.albumPostCommentId = albumPostCommentId;
        this.content = content;
        this.parent = parent;
        this.commentGroupId = commentGroupId;
        this.childList = childList;
        this.albumPost = albumPost;
        this.author = author;
        this.likeCount = likeCount;
        this.isDeleted = isDeleted;
    }

    public void addChild(AlbumPostComment child){
        childList.add(child);
    }

    public void update(String content) {
        if(hasText(content))
            this.content = content;
    }

    public void registerAlbumPost(AlbumPost albumPost) {
        this.albumPost = albumPost;
        albumPost.getComments().add(this);
    }
    public void registerParentAlbumPostComment(AlbumPostComment parent) {
        this.parent = parent;
        parent.addChild(this);
    }
}
