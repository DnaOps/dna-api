package dgu.edu.dnaapi.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
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

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<NoticeComments> comments;

    private int likeCount;

    @Builder
    public Notices(Long noticeId, String title, String content, User author, List<NoticeComments> comments, int likeCount) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.author = author;
        this.comments = comments;
        this.likeCount = likeCount;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}




