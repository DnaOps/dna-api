package dgu.edu.dnaapi.domain.dto.noticePost;

import dgu.edu.dnaapi.domain.NoticePost;
import lombok.Getter;

@Getter
public class NoticePostResponseDto extends NoticePostMetaDataResponseDto {

    private String content;
    private Boolean isLikedByUser;

    public NoticePostResponseDto(NoticePost entity, Boolean isLikedByUser) {
        super(entity);
        this.content = entity.getContent();
        this.isLikedByUser = isLikedByUser;
    }
}
