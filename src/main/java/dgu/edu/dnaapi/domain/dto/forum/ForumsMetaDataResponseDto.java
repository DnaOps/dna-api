package dgu.edu.dnaapi.domain.dto.forum;

import dgu.edu.dnaapi.domain.Forums;
import lombok.Getter;

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
}
