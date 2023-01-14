package dgu.edu.dnaapi.domain.dto.noticeComments;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeCommentsUpdateDto {
    private Long commentId;
    private String content;

    @Builder
    public NoticeCommentsUpdateDto(Long commentId, String content) {
        this.commentId = commentId;
        this.content = content;
    }
}
