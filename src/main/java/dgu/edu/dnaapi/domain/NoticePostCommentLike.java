package dgu.edu.dnaapi.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class NoticePostCommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticePostCommentLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commentId")
    private NoticePostComment noticePostComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Builder
    public NoticePostCommentLike(NoticePostComment noticePostComment, User user) {
        this.noticePostComment = noticePostComment;
        this.user = user;
    }
}
