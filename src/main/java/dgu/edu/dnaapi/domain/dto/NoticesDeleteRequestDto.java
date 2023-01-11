package dgu.edu.dnaapi.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticesDeleteRequestDto {
    private Long noticeId;

    @Builder
    public NoticesDeleteRequestDto(Long noticeId) {
        this.noticeId = noticeId;
    }
}
