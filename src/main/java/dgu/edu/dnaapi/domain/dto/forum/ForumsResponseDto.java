package dgu.edu.dnaapi.domain.dto.forum;

import dgu.edu.dnaapi.domain.Forums;
import lombok.Getter;

@Getter
public class ForumsResponseDto extends ForumsMetaDataResponseDto{

    private String content;
    private boolean isLikedByUser;

    public ForumsResponseDto(Forums entity, boolean isLikedByUser){
        super(entity);
        this.content = entity.getContent();
        this.isLikedByUser = isLikedByUser;
    }
}
