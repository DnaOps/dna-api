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

    @Builder
    public NoticePostSaveRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public NoticePost toEntity(User user) {
        return NoticePost.builder()
                .title(title)
                .content(content)
                .author(user)
                .build();
    }
}
