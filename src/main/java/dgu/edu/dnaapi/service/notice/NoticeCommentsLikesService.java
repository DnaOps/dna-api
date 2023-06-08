package dgu.edu.dnaapi.service.notice;

import dgu.edu.dnaapi.repository.notice.NoticeCommentsLikesRepository;
import dgu.edu.dnaapi.repository.notice.NoticeCommentsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class NoticeCommentsLikesService {

    private final NoticeCommentsLikesRepository noticeCommentsLikesRepository;
    private final NoticeCommentsRepository noticeCommentsRepository;

}
