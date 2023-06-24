package dgu.edu.dnaapi.domain.dto.noticePost;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticePostUpdateRequestDto {

    private String title;
    private String content;
    private Boolean isPinned;

    @Builder
    public NoticePostUpdateRequestDto(String title, String content, Boolean isPinned) {
        this.title = title;
        this.content = content;
        this.isPinned = isPinned;
    }
}
