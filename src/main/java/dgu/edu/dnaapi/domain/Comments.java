package dgu.edu.dnaapi.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Comments extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId")
    private Comments parent;

    @OneToMany(mappedBy = "parent")
    private List<Comments> childList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "noticeId", nullable = false)
    private Notices notice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User author;

    private int likeCount;

    @Builder
    public Comments(Notices notice, User author, String content, Comments parent, int likeCount) {
        this.notice = notice;
        this.author = author;
        this.content = content;
        this.parent = parent;
        this.likeCount = likeCount;
    }

    public void addChild(Comments child){
        childList.add(child);
    }

    public void update(String content) {
        this.content = content;
    }
}
