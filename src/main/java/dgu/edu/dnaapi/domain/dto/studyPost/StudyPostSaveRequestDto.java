package dgu.edu.dnaapi.domain.dto.studyPost;

import dgu.edu.dnaapi.domain.StudyPost;
import dgu.edu.dnaapi.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class StudyPostSaveRequestDto {

    private String title;
    private String content;

    @Builder
    public StudyPostSaveRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public StudyPost toStudyPost(User user) {
        return StudyPost.builder()
                .title(title)
                .content(content)
                .author(user)
                .build();
    }
}
