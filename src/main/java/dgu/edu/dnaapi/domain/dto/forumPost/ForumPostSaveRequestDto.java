package dgu.edu.dnaapi.domain.dto.forumPost;

import dgu.edu.dnaapi.domain.ForumPost;
import dgu.edu.dnaapi.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ForumPostSaveRequestDto {

    private String title;
    private String content;

    @Builder
    public ForumPostSaveRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public ForumPost toEntity(User user) {
        return ForumPost.builder()
                .title(title)
                .content(content)
                .author(user)
                .build();
    }
}
