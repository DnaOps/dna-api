package dgu.edu.dnaapi.domain.dto.noticePost;

import dgu.edu.dnaapi.domain.response.ListResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
public class NoticePostMetaDataResponse {

    private ListResponse pinnedNoticePost;
    private ListResponse NoticePost;

    @Builder
    public NoticePostMetaDataResponse(ListResponse pinnedNoticePost, ListResponse noticePost) {
        this.pinnedNoticePost = pinnedNoticePost;
        NoticePost = noticePost;
    }
}
