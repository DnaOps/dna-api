package dgu.edu.dnaapi.service;

import dgu.edu.dnaapi.repository.albumPost.AlbumPostCommentRepository;
import dgu.edu.dnaapi.repository.forumPost.ForumPostCommentRepository;
import dgu.edu.dnaapi.repository.noticePost.NoticePostCommentRepository;
import dgu.edu.dnaapi.repository.studyPost.StudyPostCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostCommentService {

    private final NoticePostCommentRepository noticePostCommentRepository;
    private final ForumPostCommentRepository forumPostCommentRepository;
    private final AlbumPostCommentRepository albumPostCommentRepository;
    private final StudyPostCommentRepository studyPostCommentRepository;

    public Long getPostCommentCountByUserId(Long userId) {
        return noticePostCommentRepository.getNoticePostCountByUserId(userId)
                + forumPostCommentRepository.getForumPostCommentCountByUserId(userId)
                + albumPostCommentRepository.getAlbumPostCommentCountByUserId(userId)
                + studyPostCommentRepository.getStudyPostCommentCountByUserId(userId);
    }
}
