package dgu.edu.dnaapi.repository.forumPost;


import dgu.edu.dnaapi.controller.dto.PostSearchCondition;
import dgu.edu.dnaapi.domain.ForumPost;
import dgu.edu.dnaapi.domain.dto.forumPost.ForumPostMetaDataResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ForumPostRepositoryCustom {

    List<ForumPostMetaDataResponseDto> search(PostSearchCondition condition, Pageable pageable);
    Optional<ForumPost> findWithForumPostCommentsByForumPostId(Long forumPostId);
    Long getForumPostCountByUserId(Long userId);
}
