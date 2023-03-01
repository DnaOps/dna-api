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

    @Builder
    public CommentsSaveRequestDto(String content, Long postId, Long parentCommentId) {
        this.content = content;
        this.postId = postId;
        this.parentCommentId = parentCommentId;
    }

    public ForumComments toForumComments(User user) {
        ForumComments comments = ForumComments.builder()
                .content(content)
                .author(user)
                .parentCommentId(parentCommentId)
                .build();

        return comments;
    }
}
