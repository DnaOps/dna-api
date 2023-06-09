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
public class ForumPostComment extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long forumPostCommentId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId")
    private ForumPostComment parent;

    private Long commentGroupId;

    @OneToMany(mappedBy = "parent")
    private List<ForumPostComment> childList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forumPostId", nullable = false)
    private ForumPost forumPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId") // todo: 고민 nullable true or false?
    private User author;

    private int likeCount;

    private boolean isDeleted = false;

    @Builder
    public ForumPostComment(ForumPost forumPost, User author, String content, ForumPostComment parent, Long commentGroupId, int likeCount, boolean isDeleted) {
        this.forumPost = forumPost;
        this.author = author;
        this.content = content;
        this.parent = parent;
        this.commentGroupId = commentGroupId;
        this.likeCount = likeCount;
        this.isDeleted = isDeleted;
    }

    public void addChild(ForumPostComment child){
        childList.add(child);
    }

    public void update(String content) {
        this.content = content;
    }

    public void registerForumPost(ForumPost forumPost) {
        this.forumPost = forumPost;
        forumPost.getComments().add(this);
    }
    public void registerParentForumPostComment(ForumPostComment parent) {
        this.parent = parent;
        parent.addChild(this);
    }

    public void registerAuthor(User user) {
        this.author = user;
    }
}
