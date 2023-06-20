package dgu.edu.dnaapi.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class StudyPostLike extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studyPostLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studyPostId")
    private StudyPost studyPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Builder
    public StudyPostLike(StudyPost studyPost, User user) {
        this.studyPost = studyPost;
        this.user = user;
    }
}
