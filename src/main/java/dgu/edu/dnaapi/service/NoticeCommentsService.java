package dgu.edu.dnaapi.service;

import dgu.edu.dnaapi.domain.NoticeComments;
import dgu.edu.dnaapi.domain.Notices;
import dgu.edu.dnaapi.domain.dto.*;
import dgu.edu.dnaapi.repository.NoticesCommentsRepository;
import dgu.edu.dnaapi.repository.NoticesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NoticeCommentsService {
    private final NoticesRepository noticesRepository;
    private final NoticesCommentsRepository noticesCommentsRepository;

    @Transactional
    public Long save(NoticeCommentsSaveDto requestDto) {
        NoticeComments comment = requestDto.toEntity();
        Long noticeId = requestDto.getNoticeId();
        Long parentCommentId = requestDto.getParentCommentId();

        Notices notice = noticesRepository.findById(noticeId).orElseThrow(
                () -> new IllegalArgumentException("해당 글이 없습니다. id=" + noticeId));
        comment.registerNotice(notice);

        if(parentCommentId != null) {
            NoticeComments parent = noticesCommentsRepository.findById(parentCommentId).orElseThrow(
                    () -> new IllegalArgumentException("해당 댓글이 없습니다. id=" + parentCommentId));
            comment.registerParent(parent);
            parent.addChild(comment);
        }

        return noticesCommentsRepository.save(comment).getCommentId();
    }

    @Transactional
    public Long update(NoticeCommentsUpdateDto requestDto) {
        Long id = requestDto.getCommentId();
        NoticeComments notices = noticesCommentsRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글이 없습니다. id=" + id));
        notices.update(requestDto.getContent());
        return id;
    }

    public NoticeCommentsResponseDto findById(Long id) {
        NoticeComments entity = noticesCommentsRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
        return new NoticeCommentsResponseDto(entity);
    }

    @Transactional
    public Long delete(NoticeCommentsDeleteDto requestDto) {
        // todo 나중에 대댓글 삭제 조건 처리 등 추가 해야함
        Long deleteId = requestDto.getCommentId();
        noticesCommentsRepository.deleteById(deleteId);

        return deleteId;
    }

    public List<NoticeCommentsResponseDto> findAll(Long noticeId) {
        List<NoticeComments> commentsList = noticesCommentsRepository.findAll();
        return commentsList.stream()
                .filter(c -> c.getNotice().getNoticeId().equals(noticeId))
                .map(NoticeCommentsResponseDto::new).collect(Collectors.toList());
    }

}
