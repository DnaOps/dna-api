package dgu.edu.dnaapi.domain.dto.noticePost;

import com.querydsl.core.annotations.QueryProjection;
import dgu.edu.dnaapi.domain.NoticePost;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticePostMetaDataResponseDto {

    private Long noticeId;
    private String title;
    private String author;
    private Long authorId;
    private int commentCount;
    private int likeCount;
    private int level;
    private String modifiedAt;
    private Boolean isPinned;

    public NoticePostMetaDataResponseDto(NoticePost entity) {
        this.noticeId = entity.getNoticePostId();
        this.title = entity.getTitle();
        this.author = entity.getAuthor().getUserName();
        this.authorId = entity.getAuthor().getId();
        this.commentCount = entity.getCommentCount();
        this.modifiedAt = entity.getLastModifiedDate().toString();
        this.likeCount = entity.getLikeCount();
        this.level = entity.getAuthor().getLevel();
        this.isPinned = entity.isPinned();
    }

    @QueryProjection
    public NoticePostMetaDataResponseDto(Long noticeId, String title, String author, Long authorId, int commentCount, int likeCount, int level, LocalDateTime modifiedAt, Boolean isPinned) {
        this.noticeId = noticeId;
        this.title = title;
        this.author = author;
        this.authorId = authorId;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.level = level;
        this.modifiedAt = modifiedAt.toString();
        this.isPinned = isPinned;
    }
}
