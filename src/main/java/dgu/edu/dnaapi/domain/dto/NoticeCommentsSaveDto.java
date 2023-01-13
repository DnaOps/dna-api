package dgu.edu.dnaapi.domain.dto;

import dgu.edu.dnaapi.domain.NoticeComments;
import dgu.edu.dnaapi.domain.Notices;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeCommentsSaveDto {
    private String content;
    private Long noticeId;
    private Long parentCommentId;

    @Builder
    public NoticeCommentsSaveDto(String content, Long noticeId, Long parentCommentId) {
        this.content = content;
        this.noticeId = noticeId;
        this.parentCommentId = parentCommentId;
    }

    public NoticeComments toEntity() {
        NoticeComments comments = NoticeComments.builder()
                .content(content)
                .parentCommentId(parentCommentId)
                .build();

        return comments;
    }
}
