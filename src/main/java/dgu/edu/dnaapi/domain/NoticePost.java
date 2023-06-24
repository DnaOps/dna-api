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

@Getter
@NoArgsConstructor
@Entity
@Builder
public class NoticePost extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticePostId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User author;

    @OneToMany(mappedBy = "noticePost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticePostComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "noticePost", cascade = CascadeType.ALL)
    private Set<NoticePostLike> likes = new HashSet<>();

    @Column(columnDefinition = "int default 0", nullable = false)
    @Builder.Default
    private int likeCount = 0;

    @Column(columnDefinition = "int default 0", nullable = false)
    @Builder.Default
    private int commentCount = 0;

    @Column(columnDefinition = "boolean default false", nullable = false)
    @Builder.Default
    private boolean isPinned = false;

    public NoticePost(Long noticePostId, String title, String content, User author, List<NoticePostComment> comments,
                      Set<NoticePostLike> likes, int likeCount, int commentCount, boolean isPinned) {
        this.noticePostId = noticePostId;
        this.title = title;
        this.content = content;
        this.author = author;
        this.comments = comments;
        this.likes = likes;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.isPinned = isPinned;
    }

    public void update(String title, String content, Boolean isPinned) {
        if(hasText(title))
            this.title = title;
        if(hasText(content))
            this.content = content;
        if(isPinned != null)
            this.isPinned = isPinned;
    }
}




