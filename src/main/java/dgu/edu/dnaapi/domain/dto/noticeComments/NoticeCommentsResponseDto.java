package dgu.edu.dnaapi.domain.dto.noticeComments;

import dgu.edu.dnaapi.domain.NoticeComments;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.response.ListResponse;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class NoticeCommentsResponseDto {
    private Long commentId;
    private String content;
    private String author;
    private Long authorId;
    private Long parentId;
    private String modifiedAt;
    private ListResponse childrenComments;

    public NoticeCommentsResponseDto(NoticeComments entity) {
        this.commentId = entity.getCommentId();
        this.content = entity.getContent();
        // todo 나중에 user 매핑되도록 (or nullable -> Optional)
        User user = entity.getAuthor();
        if  (author != null) {
            this.author = user.getUserName();
            this.authorId = user.getId();
        }
        // todo 나중에 Optional 처리?
        NoticeComments parent = entity.getParent();
        if (parent != null)
            this.parentId = parent.getCommentId();

        this.modifiedAt = entity.getLastModifiedDate().toString();

        if(!entity.getChildList().isEmpty()) {
            List<NoticeCommentsResponseDto> children = entity.getChildList()
                    .stream().map(NoticeCommentsResponseDto::new).collect(Collectors.toList());
            this.childrenComments = ListResponse.builder()
                    .list(children)
                    .totalCount(children.size())
                    .build();
        }

    }
}
