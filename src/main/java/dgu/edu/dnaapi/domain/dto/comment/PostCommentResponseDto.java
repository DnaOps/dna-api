package dgu.edu.dnaapi.domain.dto.comment;

import dgu.edu.dnaapi.domain.AlbumPostComment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class PostCommentResponseDto {

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
    private Boolean isCommentLikedByUser;
    private Boolean isDeleted;
    private List<PostCommentResponseDto> childrenComments;

    public static PostCommentResponseDto convertAlbumCommentToResponseDto(AlbumPostComment albumPostComment){
        Long parentCommentId = (albumPostComment.getParent() != null) ? albumPostComment.getParent().getAlbumPostCommentId() : null;
        String comment = ((albumPostComment.getIsDeleted() == true ) ? "삭제된 댓글입니다." : albumPostComment.getContent());
        return PostCommentResponseDto.builder()
                .commentId(albumPostComment.getAlbumPostCommentId())
                .commentGroupId(albumPostComment.getCommentGroupId())
                .content(comment)
                .author(albumPostComment.getAuthor().getUserName())
                .authorId(albumPostComment.getAuthor().getId())
                .parentCommentId(parentCommentId)
                .createdAt(albumPostComment.getCreatedDate().toString())
                .modifiedAt(albumPostComment.getLastModifiedDate().toString())
                .isCommentLikedByUser(false)
                .isDeleted(albumPostComment.getIsDeleted())
                .level(albumPostComment.getAuthor().getLevel())
                .likeCount(albumPostComment.getLikeCount())
                .childrenComments(new ArrayList<>())
                .build();
    }

    public void likedByUser() {
        this.isCommentLikedByUser = true;
    }
}


