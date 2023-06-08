package dgu.edu.dnaapi.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class NoticeCommentsLikes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeCommentLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commentId")
    private NoticeComments noticeComments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Builder
    public NoticeCommentsLikes(NoticeComments noticeComments, User user) {
        this.noticeComments = noticeComments;
        this.user = user;
    }
}
