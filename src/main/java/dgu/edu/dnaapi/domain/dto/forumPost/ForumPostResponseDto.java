package dgu.edu.dnaapi.domain.dto.forumPost;

import dgu.edu.dnaapi.domain.ForumPost;
import lombok.Getter;

@Getter
public class ForumPostResponseDto extends ForumPostMetaDataResponseDto {

    private String content;
    private Boolean isLikedByUser;

    public ForumPostResponseDto(ForumPost entity, Boolean isLikedByUser){
        super(entity);
        this.content = entity.getContent();
        this.isLikedByUser = isLikedByUser;
    }
}
