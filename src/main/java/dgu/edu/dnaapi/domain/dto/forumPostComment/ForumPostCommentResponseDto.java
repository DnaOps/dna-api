package dgu.edu.dnaapi.domain.dto.forumPostComment;

import dgu.edu.dnaapi.domain.ForumPostComment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class ForumPostCommentResponseDto {

    private Long commentId;
    private String content;
    private String author;
    private Long authorId;
    private Long commentGroupId;
    private Long parentCommentId;
    private String createdAt;
    private String modifiedAt;
    private int level;
    private int likeCount;
    private Boolean isForumCommentLikedByUser;
    private Boolean isDeleted;
    private List<ForumPostCommentResponseDto> childrenComments;

    public static ForumPostCommentResponseDto convertForumCommentToResponseDto(ForumPostComment forumPostComment){
        Long parentCommentId = (forumPostComment.getParent() != null) ? forumPostComment.getParent().getForumPostCommentId() : null;
        String comment = ((forumPostComment.isDeleted() == true ) ? "삭제된 댓글입니다." : forumPostComment.getContent());
        return ForumPostCommentResponseDto.builder()
                .commentId(forumPostComment.getForumPostCommentId())
                .commentGroupId(forumPostComment.getCommentGroupId())
                .content(comment)
                .author(forumPostComment.getAuthor().getUserName())
                .authorId(forumPostComment.getAuthor().getId())
                .parentCommentId(parentCommentId)
                .createdAt(forumPostComment.getCreatedDate().toString())
                .modifiedAt(forumPostComment.getLastModifiedDate().toString())
                .isForumCommentLikedByUser(false)
                .isDeleted(forumPostComment.isDeleted())
                .level(forumPostComment.getAuthor().getLevel())
                .likeCount(forumPostComment.getLikeCount())
                .childrenComments(new ArrayList<>())
                .build();
    }

    public void likedByUser() {
        this.isForumCommentLikedByUser = true;
    }
}
