package dgu.edu.dnaapi.domain.dto.forumComments;

import dgu.edu.dnaapi.domain.ForumComments;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.response.ListResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class ForumCommentsResponseDto {

    private Long commentId;
    private String content;
    private String author;
    private Long authorId;
    private Long commentGroupId;
    private Long parentCommentId;
    private String createdAt;
    private String modifiedAt;
    private int level;
    private int likeCount;
    private List<ForumCommentsResponseDto> childrenComments;

    public static ForumCommentsResponseDto convertForumCommentToResponseDto(ForumComments forumComments){
        Long parentCommentId = (forumComments.getParent() != null) ? forumComments.getParent().getCommentId() : null;
        return ForumCommentsResponseDto.builder()
                .commentId(forumComments.getCommentId())
                .commentGroupId(forumComments.getCommentGroupId())
                .content(forumComments.getContent())
                .author(forumComments.getAuthor().getUserName())
                .authorId(forumComments.getAuthor().getId())
                .parentCommentId(parentCommentId)
                .createdAt(forumComments.getCreatedDate().toString())
                .modifiedAt(forumComments.getLastModifiedDate().toString())
                .level(forumComments.getAuthor().getLevel())
                .likeCount(forumComments.getLikeCount())
                .childrenComments(new ArrayList<>())
                .build();
    }
}
