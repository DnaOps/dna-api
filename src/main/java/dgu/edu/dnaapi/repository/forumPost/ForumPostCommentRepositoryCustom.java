package dgu.edu.dnaapi.repository.forumPost;


import dgu.edu.dnaapi.domain.ForumPostComment;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ForumPostCommentRepositoryCustom {

    List<ForumPostComment> search(Long forumPostId, Long start, Pageable pageable);
    List<ForumPostComment> findAllReplyForumPostComments(Long forumPostId, List<Long> commentsId);
    List<ForumPostComment> findAllReplyForumPostCommentsByCommentGroupId(Long forumPostId, Long commentGroupId);
    List<ForumPostComment> findAllForumPostCommentsByForumPostId(Long forumPostId);
}
