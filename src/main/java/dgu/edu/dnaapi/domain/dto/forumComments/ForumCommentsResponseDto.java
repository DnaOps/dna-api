package dgu.edu.dnaapi.domain.dto.forumComments;

import dgu.edu.dnaapi.domain.ForumComments;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.response.ListResponse;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ForumCommentsResponseDto {

    private Long commentId;
    private String content;
    private String author;
    private Long authorId;
    private Long parentId;
    private String modifiedAt;
    private ListResponse childrenComments;
    private int level;
    private int likeCount;

    public ForumCommentsResponseDto(ForumComments entity) {
        this.commentId = entity.getCommentId();
        this.content = entity.getContent();

        User author = entity.getAuthor();
        if  (author != null) {
            this.author = author.getUserName();
            this.authorId = author.getId();
            this.level = author.getLevel();
        }
        ForumComments parent = entity.getParent();

        if (parent != null)
            this.parentId = parent.getCommentId();

        this.modifiedAt = entity.getLastModifiedDate().toString();

        if(!entity.getChildList().isEmpty()) {
            List<ForumCommentsResponseDto> children = entity.getChildList()
                    .stream().map(ForumCommentsResponseDto::new).collect(Collectors.toList());
            this.childrenComments = ListResponse.builder()
                    .list(children)
                    .totalCount(children.size())
                    .build();
        }
        // todo comment like
        this.likeCount = 0;
    }
}
