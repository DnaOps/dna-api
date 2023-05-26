package dgu.edu.dnaapi.domain.dto.forum;

import com.querydsl.core.annotations.QueryProjection;
import dgu.edu.dnaapi.domain.Forums;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ForumsMetaDataResponseDto {

    private Long forumId;
    private String title;
    private String author;
    private Long authorId;
    private int commentCount;
    private int likeCount;
    private int level;
    private String modifiedAt;

    public ForumsMetaDataResponseDto(Forums entity) {
        this.forumId = entity.getForumId();
        this.title = entity.getTitle();
        this.author = entity.getAuthor().getUserName();
        this.authorId = entity.getAuthor().getId();
        this.commentCount = entity.getComments().size();
        this.modifiedAt = entity.getLastModifiedDate().toString();
        this.likeCount = entity.getLikes().size();
        this.level = entity.getAuthor().getLevel();
    }

    @QueryProjection
    public ForumsMetaDataResponseDto(Long forumId, String title, String author, Long authorId, int commentCount, int likeCount, int level, LocalDateTime modifiedAt) {
        this.forumId = forumId;
        this.title = title;
        this.author = author;
        this.authorId = authorId;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.level = level;
        this.modifiedAt = modifiedAt.toString();
    }
}
