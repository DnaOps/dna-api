package dgu.edu.dnaapi.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Getter
@NoArgsConstructor
@Entity
public class NoticePostComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticePostCommentId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId")
    private NoticePostComment parent;

    private Long commentGroupId;

    @OneToMany(mappedBy = "parent")
    private List<NoticePostComment> childList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "noticeId", nullable = false)
    private NoticePost noticePost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId") // todo: 고민 nullable true or false?
    private User author;

    private int likeCount;
    private boolean isDeleted = false;

    @Builder
    public NoticePostComment(NoticePost noticePost, User author, String content, NoticePostComment parent, Long commentGroupId, int likeCount, boolean isDeleted) {
        this.noticePost = noticePost;
        this.author = author;
        this.content = content;
        this.parent = parent;
        this.commentGroupId = commentGroupId;
        this.likeCount = likeCount;
        this.isDeleted = isDeleted;
    }

    public void addChild(NoticePostComment child){
        childList.add(child);
    }

    public void update(String content) {
        if(hasText(content))
            this.content = content;
    }

    public void registerNoticePost(NoticePost noticePost) {
        this.noticePost = noticePost;
        noticePost.getComments().add(this);
    }
    public void registerParentNoticeComment(NoticePostComment parent) {
        this.parent = parent;
        parent.addChild(this);
    }

    public void registerAuthor(User user) {
        this.author = user;
    }
}
