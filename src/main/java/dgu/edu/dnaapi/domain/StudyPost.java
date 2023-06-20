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

@Entity
@NoArgsConstructor
@Getter
@Builder
public class StudyPost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studyPostId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User author;

    @OneToMany(mappedBy = "studyPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyPostComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "studyPost", cascade = CascadeType.ALL)
    private Set<StudyPostLike> likes = new HashSet<>();

    @Column(columnDefinition = "int default 0", nullable = false)
    @Builder.Default
    private int likeCount = 0;

    @Column(columnDefinition = "int default 0", nullable = false)
    @Builder.Default
    private int commentCount = 0;

    public StudyPost(Long studyPostId, String title, String content, User author, List<StudyPostComment> comments, Set<StudyPostLike> likes, int likeCount, int commentCount) {
        this.studyPostId = studyPostId;
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
