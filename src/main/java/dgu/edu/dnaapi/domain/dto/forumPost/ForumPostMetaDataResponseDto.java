package dgu.edu.dnaapi.domain.dto.forumPost;

import com.querydsl.core.annotations.QueryProjection;
import dgu.edu.dnaapi.domain.ForumPost;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ForumPostMetaDataResponseDto {

    private Long forumId;
    private String title;
    private String author;
    private Long authorId;
    private int commentCount;
    private int likeCount;
    private int level;
    private String modifiedAt;

    public ForumPostMetaDataResponseDto(ForumPost entity) {
        this.forumId = entity.getForumPostId();
        this.title = entity.getTitle();
        this.author = entity.getAuthor().getUserName();
        this.authorId = entity.getAuthor().getId();
        this.commentCount = entity.getComments().size();
        this.modifiedAt = entity.getLastModifiedDate().toString();
        this.likeCount = entity.getLikes().size();
        this.level = entity.getAuthor().getLevel();
    }

    @QueryProjection
    public ForumPostMetaDataResponseDto(Long forumId, String title, String author, Long authorId, int commentCount, int likeCount, int level, LocalDateTime modifiedAt) {
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
