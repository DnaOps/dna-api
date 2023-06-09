package dgu.edu.dnaapi.domain.dto.noticePostComment;

import dgu.edu.dnaapi.domain.ForumPostComment;
import dgu.edu.dnaapi.domain.NoticePostComment;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.dto.forumPostComment.ForumPostCommentResponseDto;
import dgu.edu.dnaapi.domain.response.ListResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class NoticePostCommentResponseDto {

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
    private Boolean isNoticeCommentLikedByUser;
    private Boolean isDeleted;
    private List<NoticePostCommentResponseDto> childrenComments;

    public static NoticePostCommentResponseDto convertNoticeCommentToResponseDto(NoticePostComment noticePostComment){
        Long parentCommentId = (noticePostComment.getParent() != null) ? noticePostComment.getParent().getNoticePostCommentId() : null;
        String content = ((noticePostComment.isDeleted() == true ) ? "삭제된 댓글입니다." : noticePostComment.getContent());
        return NoticePostCommentResponseDto.builder()
                .commentId(noticePostComment.getNoticePostCommentId())
                .commentGroupId(noticePostComment.getCommentGroupId())
                .content(content)
                .author(noticePostComment.getAuthor().getUserName())
                .authorId(noticePostComment.getAuthor().getId())
                .parentCommentId(parentCommentId)
                .createdAt(noticePostComment.getCreatedDate().toString())
                .modifiedAt(noticePostComment.getLastModifiedDate().toString())
                .isNoticeCommentLikedByUser(false)
                .isDeleted(noticePostComment.isDeleted())
                .level(noticePostComment.getAuthor().getLevel())
                .likeCount(noticePostComment.getLikeCount())
                .childrenComments(new ArrayList<>())
                .build();
    }

    public void likedByUser() {
        this.isNoticeCommentLikedByUser = true;
    }
}
