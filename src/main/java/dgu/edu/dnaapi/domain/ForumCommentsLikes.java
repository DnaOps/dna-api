package dgu.edu.dnaapi.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class ForumCommentsLikes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long forumCommentLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commentId")
    private ForumComments forumComments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Builder
    public ForumCommentsLikes(ForumComments forumComments, User user) {
        this.forumComments = forumComments;
        this.user = user;
    }
}
