package dgu.edu.dnaapi.domain.dto.noticePost;

import dgu.edu.dnaapi.domain.NoticePost;
import dgu.edu.dnaapi.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticePostSaveRequestDto {
    private String title;
    private String content;
    private Boolean isPinned;

    @Builder
    public NoticePostSaveRequestDto(String title, String content, Boolean isPinned) {
        this.title = title;
        this.content = content;
        this.isPinned = isPinned;
    }

    public NoticePost toEntity(User user) {
        boolean isPinned = getIsPinned() != null && getIsPinned();
        return NoticePost.builder()
                .title(title)
                .content(content)
                .isPinned(isPinned)
                .author(user)
                .build();
    }
}
