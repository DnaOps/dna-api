package dgu.edu.dnaapi.domain.dto.comment;

import dgu.edu.dnaapi.domain.ForumPostComment;
import dgu.edu.dnaapi.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CommentSaveRequestDto {
    private String content;
    private Long parentCommentId;
    private Long commentGroupId;

    @Builder
    public CommentSaveRequestDto(String content, Long parentCommentId, Long commentGroupId) {
        this.content = content;
        this.parentCommentId = parentCommentId;
        this.commentGroupId = commentGroupId;
    }

    public ForumPostComment toForumComment(User user) {
        ForumPostComment comments = ForumPostComment.builder()
                .content(content)
                .author(user)
                .commentGroupId(commentGroupId)
                .isDeleted(false)
                .build();

        return comments;
    }
}
