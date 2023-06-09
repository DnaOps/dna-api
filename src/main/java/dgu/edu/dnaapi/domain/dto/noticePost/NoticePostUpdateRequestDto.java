package dgu.edu.dnaapi.domain.dto.noticePost;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticePostUpdateRequestDto {

    private String title;
    private String content;

    @Builder
    public NoticePostUpdateRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
