package dgu.edu.dnaapi.domain.dto;

import dgu.edu.dnaapi.domain.Notices;
import dgu.edu.dnaapi.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticesSaveRequestDto {
    private String title;
    private String content;

    @Builder
    public NoticesSaveRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Notices toEntity(User user) {
        return Notices.builder()
                .title(title)
                .content(content)
                .author(user)
                .build();
    }
}
