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
    private User author;

    @Builder
    public NoticesSaveRequestDto(String title, String content, User author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public Notices toEntity() {
        return Notices.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}
