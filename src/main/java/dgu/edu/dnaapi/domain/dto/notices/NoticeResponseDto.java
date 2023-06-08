package dgu.edu.dnaapi.domain.dto.notices;

import dgu.edu.dnaapi.domain.Notices;
import lombok.Getter;

@Getter
public class NoticeResponseDto extends NoticesMetaDataResponseDto{

    private String content;
    private Boolean isLikedByUser;

    public NoticeResponseDto(Notices entity,Boolean isLikedByUser) {
        super(entity);
        this.content = entity.getContent();
        this.isLikedByUser = isLikedByUser;
    }
}
