package dgu.edu.dnaapi.domain.dto;

import dgu.edu.dnaapi.domain.NoticeComments;
import dgu.edu.dnaapi.domain.Notices;
import dgu.edu.dnaapi.domain.response.ListResponse;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class NoticesResponseDto {

    private Long noticeId;
    private String title;
    private String content;
    private String author;
    private Long authorId;
    private int commentCount;
    private String modifiedAt;

    public NoticesResponseDto(Notices entity) {
        this.noticeId = entity.getNoticeId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
//        this.author = entity.getAuthor().getUsername();
//        this.authorId = entity.getAuthor().getId();
        this.commentCount = entity.getComments().size();
        this.modifiedAt = entity.getLastModifiedDate().toString();
    }

}
