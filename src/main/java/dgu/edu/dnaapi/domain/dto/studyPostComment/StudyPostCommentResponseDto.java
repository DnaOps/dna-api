package dgu.edu.dnaapi.domain.dto.studyPostComment;

import dgu.edu.dnaapi.domain.StudyPostComment;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class StudyPostCommentResponseDto {

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
    private List<StudyPostCommentResponseDto> childrenComments;

    public static StudyPostCommentResponseDto convertStudyPostCommentToResponseDto(StudyPostComment studyPostComment){
        Long parentCommentId = (studyPostComment.getParent() != null) ? studyPostComment.getParent().getStudyPostCommentId() : null;
        String content = ((studyPostComment.isDeleted() == true ) ? "삭제된 댓글입니다." : studyPostComment.getContent());
        return StudyPostCommentResponseDto.builder()
                .commentId(studyPostComment.getStudyPostCommentId())
                .commentGroupId(studyPostComment.getCommentGroupId())
                .content(content)
                .author(studyPostComment.getAuthor().getUserName())
                .authorId(studyPostComment.getAuthor().getId())
                .parentCommentId(parentCommentId)
                .createdAt(studyPostComment.getCreatedDate().toString())
                .modifiedAt(studyPostComment.getLastModifiedDate().toString())
                .isCommentLikedByUser(false)
                .isDeleted(studyPostComment.isDeleted())
                .level(studyPostComment.getAuthor().getLevel())
                .likeCount(studyPostComment.getLikeCount())
                .childrenComments(new ArrayList<>())
                .build();
    }

    public void likedByUser() {
        this.isCommentLikedByUser = true;
    }
}
