package dgu.edu.dnaapi.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeCommentsDeleteDto {
    private Long commentId;

    @Builder
    public NoticeCommentsDeleteDto(Long commentId) {
        this.commentId = commentId;
    }
}
