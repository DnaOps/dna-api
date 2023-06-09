package dgu.edu.dnaapi.domain.dto.comment;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CommentUpdateRequestDto {

    private String content;

    @Builder
    public CommentUpdateRequestDto(String content) {
        this.content = content;
    }
}
