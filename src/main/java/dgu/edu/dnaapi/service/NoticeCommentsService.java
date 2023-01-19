package dgu.edu.dnaapi.service;

import dgu.edu.dnaapi.domain.NoticeComments;
import dgu.edu.dnaapi.domain.Notices;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.dto.noticeComments.NoticeCommentsDeleteDto;
import dgu.edu.dnaapi.domain.dto.noticeComments.NoticeCommentsResponseDto;
import dgu.edu.dnaapi.domain.dto.noticeComments.NoticeCommentsSaveDto;
import dgu.edu.dnaapi.domain.dto.noticeComments.NoticeCommentsUpdateDto;
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
    public Long save(User user, NoticeCommentsSaveDto requestDto) {
        NoticeComments comment = requestDto.toEntity(user);
        comment.registerAuthor(user);

        Long noticeId = requestDto.getNoticeId();

        Notices notice = noticesRepository.findById(noticeId).orElseThrow(
                () -> new IllegalArgumentException("해당 글이 없습니다. id=" + noticeId));
        comment.registerNotice(notice);

        if(requestDto.getParentCommentId() != null) {
            Long parentCommentId = requestDto.getParentCommentId();
            NoticeComments parent = noticesCommentsRepository.findById(parentCommentId).orElseThrow(
                    () -> new IllegalArgumentException("해당 댓글이 없습니다. id=" + parentCommentId));
            comment.registerParent(parent);
        }

        return noticesCommentsRepository.save(comment).getCommentId();
    }

    @Transactional
    public Long update(Long userId, NoticeCommentsUpdateDto requestDto) {
        Long id = requestDto.getCommentId();
        NoticeComments comment = noticesCommentsRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글이 없습니다. id=" + id));

        if(!comment.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("작성자만 댓글을 수정할 수 있습니다.");
        }
        comment.update(requestDto.getContent());
        return id;
    }

    public NoticeCommentsResponseDto findById(Long id) {
        NoticeComments entity = noticesCommentsRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글이 없습니다. id=" + id));
        return new NoticeCommentsResponseDto(entity);
    }

    @Transactional
    public Long delete(Long userId, NoticeCommentsDeleteDto requestDto) {
        // todo 나중에 대댓글 삭제 조건 처리 등 추가 해야함
        Long deleteId = requestDto.getCommentId();
        NoticeComments comment = noticesCommentsRepository.findById(deleteId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글이 없습니다. id=" + deleteId));
        if(!comment.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("작성자만 댓글을 삭제할 수 있습니다.");
        }
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
