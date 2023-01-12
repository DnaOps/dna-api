package dgu.edu.dnaapi.domain.dto;

import dgu.edu.dnaapi.domain.Notices;
import dgu.edu.dnaapi.domain.User;
import lombok.Getter;

@Getter
public class NoticesResponseDto {
    private Long noticeId;
    private String title;
    private String content;
    private User author;

    public NoticesResponseDto(Notices entity) {
        this.noticeId = entity.getNoticeId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.author = entity.getAuthor();
    }

}
