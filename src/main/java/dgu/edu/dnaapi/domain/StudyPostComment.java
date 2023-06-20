package dgu.edu.dnaapi.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Entity
@NoArgsConstructor
@Getter
@Builder
public class StudyPostComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studyPostCommentId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId")
    private StudyPostComment parent;

    private Long commentGroupId;

    @OneToMany(mappedBy = "parent")
    private List<StudyPostComment> childList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "noticeId", nullable = false)
    private StudyPost studyPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId") // todo: 고민 nullable true or false?
    private User author;

    @Column(columnDefinition = "int default 0", nullable = false)
    @Builder.Default
    private int likeCount = 0;

    private boolean isDeleted = false;

    public StudyPostComment(Long studyPostCommentId, String content, StudyPostComment parent, Long commentGroupId, List<StudyPostComment> childList, StudyPost studyPost, User author, int likeCount, boolean isDeleted) {
        this.studyPostCommentId = studyPostCommentId;
        this.content = content;
        this.parent = parent;
        this.commentGroupId = commentGroupId;
        this.childList = childList;
        this.studyPost = studyPost;
        this.author = author;
        this.likeCount = likeCount;
        this.isDeleted = isDeleted;
    }

    public void addChild(StudyPostComment child){
        childList.add(child);
    }

    public void update(String content) {
        if(hasText(content))
            this.content = content;
    }

    public void registerStudyPost(StudyPost studyPost) {
        this.studyPost = studyPost;
        studyPost.getComments().add(this);
    }
    public void registerParentStudyComment(StudyPostComment parent) {
        this.parent = parent;
        parent.addChild(this);
    }
}
