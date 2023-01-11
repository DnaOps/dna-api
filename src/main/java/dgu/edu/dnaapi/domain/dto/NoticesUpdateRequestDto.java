package dgu.edu.dnaapi.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticesUpdateRequestDto {
    private Long noticeId;
    private String title;
    private String content;

    @Builder
    public NoticesUpdateRequestDto(Long noticeId, String title, String content) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
    }
}
