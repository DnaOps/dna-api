package dgu.edu.dnaapi.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class NoticeComments extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId")
    private NoticeComments parent;

    private Long parentCommentId;

    @OneToMany(mappedBy = "parent")
    private List<NoticeComments> childList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "noticeId", nullable = false)
    private Notices notice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId") // todo: 고민 nullable true or false?
    private User author;

    private int likeCount;

    @Builder
    public NoticeComments(Notices notice, User author, String content, NoticeComments parent, Long parentCommentId, int likeCount) {
        this.notice = notice;
        this.author = author;
        this.content = content;
        this.parent = parent;
        this.parentCommentId = parentCommentId;
        this.likeCount = likeCount;
    }

    public void addChild(NoticeComments child){
        childList.add(child);
    }

    public void update(String content) {
        this.content = content;
    }

    public void registerNotice(Notices notice) {
        this.notice = notice;
        notice.getComments().add(this);
    }
    public void registerParent(NoticeComments parent) {
        this.parent = parent;
        parent.addChild(this);
    }

    public void registerAuthor(User user) {
        this.author = user;
    }
}
