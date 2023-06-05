package dgu.edu.dnaapi.repository.forum;


import dgu.edu.dnaapi.domain.ForumComments;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ForumCommentsRepositoryCustom {

    List<ForumComments> search(Long forumId, Long start, Pageable pageable);
    List<ForumComments> findAllReplyComments(Long forumId, List<Long> commentsId);
}
