package dgu.edu.dnaapi.repository.noticePost;

import dgu.edu.dnaapi.controller.dto.PostSearchCondition;
import dgu.edu.dnaapi.domain.NoticePost;
import dgu.edu.dnaapi.domain.dto.noticePost.NoticePostMetaDataResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface NoticePostRepositoryCustom {

    List<NoticePostMetaDataResponseDto> search(PostSearchCondition condition, Pageable pageable);
    List<NoticePostMetaDataResponseDto> searchPinnedNoticePostMetaData();
    Optional<NoticePost> findWithNoticePostCommentsByNoticePostId(Long noticePostId);
}
