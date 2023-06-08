package dgu.edu.dnaapi.domain.dto.notices;

import dgu.edu.dnaapi.domain.NoticeComments;
import dgu.edu.dnaapi.domain.Notices;
import dgu.edu.dnaapi.domain.response.ListResponse;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class NoticesResponseDto extends NoticesMetaDataResponseDto{

    private String content;
    private Boolean isLikedByUser;

    public NoticesResponseDto(Notices entity, Boolean isLikedByUser) {
        super(entity);
        this.content = entity.getContent();
        this.isLikedByUser = isLikedByUser;
    }
}
