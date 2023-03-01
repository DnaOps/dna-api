package dgu.edu.dnaapi.domain.dto.forum;

import dgu.edu.dnaapi.domain.Forums;
import dgu.edu.dnaapi.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ForumSaveRequestDto {

    private String title;
    private String content;

    @Builder
    public ForumSaveRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Forums toEntity(User user) {
        return Forums.builder()
                .title(title)
                .content(content)
                .author(user)
                .build();
    }
}
