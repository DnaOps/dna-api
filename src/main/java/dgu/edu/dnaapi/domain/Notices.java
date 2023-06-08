package dgu.edu.dnaapi.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;

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
public class Notices extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User author;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeComments> comments = new ArrayList<>();

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL)
    private Set<NoticeLikes> likes = new HashSet<>();

    @Column(columnDefinition = "int default 0", nullable = false)
    @Builder.Default
    private int likeCount = 0;

    @Column(columnDefinition = "int default 0", nullable = false)
    @Builder.Default
    private int commentCount = 0;

    public Notices(Long noticeId, String title, String content, User author, List<NoticeComments> comments,
                                                    Set<NoticeLikes> likes, int likeCount, int commentCount) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.author = author;
        this.comments = comments;
        this.likes = likes;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

    public void update(String title, String content) {
        if(hasText(title))
            this.title = title;
        if(hasText(content))
            this.content = content;
    }
}




