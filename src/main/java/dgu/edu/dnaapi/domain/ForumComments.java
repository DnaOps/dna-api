package dgu.edu.dnaapi.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class ForumComments extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId")
    private ForumComments parent;

    private Long parentCommentId;

    @OneToMany(mappedBy = "parent")
    private List<ForumComments> childList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forumId", nullable = false)
    private Forums forum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId") // todo: 고민 nullable true or false?
    private User author;

    private int likeCount;

    @Builder
    public ForumComments(Forums forum, User author, String content, ForumComments parent, Long parentCommentId, int likeCount) {
        this.forum = forum;
        this.author = author;
        this.content = content;
        this.parent = parent;
        this.parentCommentId = parentCommentId;
        this.likeCount = likeCount;
    }

    public void addChild(ForumComments child){
        childList.add(child);
    }

    public void update(String content) {
        this.content = content;
    }

    public void registerForum(Forums forum) {
        this.forum = forum;
        forum.getComments().add(this);
    }
    public void registerParent(ForumComments parent) {
        this.parent = parent;
        parent.addChild(this);
    }

    public void registerAuthor(User user) {
        this.author = user;
    }
}
