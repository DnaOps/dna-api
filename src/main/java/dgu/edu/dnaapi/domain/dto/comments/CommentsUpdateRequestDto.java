package dgu.edu.dnaapi.domain.dto.comments;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CommentsUpdateRequestDto {

    private String content;

    @Builder
    public CommentsUpdateRequestDto(String content) {
        this.content = content;
    }
}
