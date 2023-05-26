package dgu.edu.dnaapi.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
public class Forums extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long forumId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User author;

    @OneToMany(mappedBy = "forum", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ForumComments> comments = new ArrayList<>();

    @OneToMany(mappedBy = "forum", cascade = CascadeType.ALL)
    private Set<ForumLikes> likes = new HashSet<>();

    @Column(columnDefinition = "int default 0", nullable = false)
    @Builder.Default
    private int likeCount = 0;

    @Column(columnDefinition = "int default 0", nullable = false)
    @Builder.Default
    private int commentCount = 0;

    @Builder
    public Forums(Long forumId, String title, String content, User author, List<ForumComments> comments, int likeCount, int commentCount) {
        this.forumId = forumId;
        this.title = title;
        this.content = content;
        this.author = author;
        this.comments = comments;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
