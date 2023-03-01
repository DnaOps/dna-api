package dgu.edu.dnaapi.domain.dto.forum;

import dgu.edu.dnaapi.domain.Forums;
import lombok.Getter;

@Getter
public class ForumsResponseDto extends ForumsMetaDataResponseDto{

    private String content;

    public ForumsResponseDto(Forums entity){
        super(entity);
        this.content = entity.getContent();
    }
}
