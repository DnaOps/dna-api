package dgu.edu.dnaapi.service;

import dgu.edu.dnaapi.repository.albumPost.AlbumPostRepository;
import dgu.edu.dnaapi.repository.forumPost.ForumPostRepository;
import dgu.edu.dnaapi.repository.noticePost.NoticePostRepository;
import dgu.edu.dnaapi.repository.studyPost.StudyPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final NoticePostRepository noticePostRepository;
    private final ForumPostRepository forumPostRepository;
    private final AlbumPostRepository albumPostRepository;
    private final StudyPostRepository studyPostRepository;

    public Long getPostCountByUserId(Long userId) {
        return noticePostRepository.getNoticePostCountByUserId(userId)
                + forumPostRepository.getForumPostCountByUserId(userId)
                + studyPostRepository.getStudyPostCountByUserId(userId)
                + albumPostRepository.getAlbumPostCountByUserId(userId);
    }
}
