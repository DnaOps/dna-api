package dgu.edu.dnaapi.domain.dto.notices;

import com.querydsl.core.annotations.QueryProjection;
import dgu.edu.dnaapi.domain.Notices;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticesMetaDataResponseDto {

    private Long noticeId;
    private String title;
    private String author;
    private Long authorId;
    private int commentCount;
    private int likeCount;
    private int level;
    private String modifiedAt;

    public NoticesMetaDataResponseDto(Notices entity) {
        this.noticeId = entity.getNoticeId();
        this.title = entity.getTitle();
        this.author = entity.getAuthor().getUserName();
        this.authorId = entity.getAuthor().getId();
        this.commentCount = entity.getComments().size();
        this.modifiedAt = entity.getLastModifiedDate().toString();
        this.likeCount = entity.getLikes().size();
        this.level = entity.getAuthor().getLevel();
    }

    @QueryProjection
    public NoticesMetaDataResponseDto(Long noticeId, String title, String author, Long authorId, int commentCount, int likeCount, int level, LocalDateTime modifiedAt) {
        this.noticeId = noticeId;
        this.title = title;
        this.author = author;
        this.authorId = authorId;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.level = level;
        this.modifiedAt = modifiedAt.toString();
    }
}
