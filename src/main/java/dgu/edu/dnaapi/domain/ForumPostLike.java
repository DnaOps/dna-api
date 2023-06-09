package dgu.edu.dnaapi.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class ForumPostLike extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long forumPostLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forumId")
    private ForumPost forumPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Builder
    public ForumPostLike(ForumPost forumPost, User user) {
        this.forumPost = forumPost;
        this.user = user;
    }
}
