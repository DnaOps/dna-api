package dgu.edu.dnaapi.service;

import dgu.edu.dnaapi.domain.NoticeLikes;
import dgu.edu.dnaapi.domain.Notices;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.repository.NoticeLikesRepository;
import dgu.edu.dnaapi.repository.NoticesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeLikesService {
    private final NoticeLikesRepository noticeLikesRepository;
    private final NoticesRepository noticesRepository;

    public String toggleLike(User user, Long noticeId) {
        Notices notice = noticesRepository.findById(noticeId).orElseThrow();
        NoticeLikes likes = noticeLikesRepository.findByUserAndNotice(user, notice);

        if(likes != null) {
            noticesRepository.decreaseLikeCount(notice.getNoticeId());
            noticeLikesRepository.delete(likes);
            return "deleted";
        }

        noticesRepository.increaseLikeCount(notice.getNoticeId());
        noticeLikesRepository.save(new NoticeLikes(notice, user));
        return "added";
    }

    public boolean isNoticeLikedByUser(User user, Notices notice) {
        return noticeLikesRepository.findByUserAndNotice(user, notice) != null;
    }
}
