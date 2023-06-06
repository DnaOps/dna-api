package dgu.edu.dnaapi.domain.dto.comments;

import dgu.edu.dnaapi.domain.ForumComments;
import dgu.edu.dnaapi.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CommentsSaveRequestDto {
    private String content;
    private Long postId;
    private Long parentCommentId;
    private Long commentGroupId;

    @Builder
    public CommentsSaveRequestDto(String content, Long postId, Long parentCommentId, Long commentGroupId) {
        this.content = content;
        this.postId = postId;
        this.parentCommentId = parentCommentId;
        this.commentGroupId = commentGroupId;
    }

    public ForumComments toForumComments(User user) {
        ForumComments comments = ForumComments.builder()
                .content(content)
                .author(user)
                .commentGroupId(commentGroupId)
                .isDeleted(false)
                .build();

        return comments;
    }
}
