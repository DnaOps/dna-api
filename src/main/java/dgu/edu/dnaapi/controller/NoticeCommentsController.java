package dgu.edu.dnaapi.controller;

import dgu.edu.dnaapi.domain.dto.*;
import dgu.edu.dnaapi.domain.response.*;
import dgu.edu.dnaapi.service.NoticeCommentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class NoticeCommentsController {

    private final NoticeCommentsService noticeCommentsService;

    @GetMapping("/comments/notices/{noticeId}")
    public ResponseEntity<Message> findAllByNoticeId(@PathVariable Long noticeId) {
        List<NoticeCommentsResponseDto> comments = noticeCommentsService.findAll(noticeId);

        ListResponse listResponse = ListResponse.builder()
                .list(comments)
                .totalCount(comments.size())
                .build();

        Message message = Message.builder()
                .data(listResponse)
                .apiStatus(new ApiStatus(StatusEnum.OK, null))
                .build();

        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    // api spec에는 없는데 게시글 Id가 dto 안에 있어야함(또는 Path에 있어야 할 것 같음)
    // url도 기존 spec에서 type으로 게시판 구분하던거 빼아함
    @PostMapping("/comments")
    public ResponseEntity<Message> save(@RequestBody NoticeCommentsSaveDto requestDto) {
        Long savedId = noticeCommentsService.save(requestDto);
        Message message = Message.builder()
                .data(savedId)
                .apiStatus(new ApiStatus(StatusEnum.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @DeleteMapping("/comments")
    public ResponseEntity<Message> delete(@RequestBody NoticeCommentsDeleteDto requestDto) {
        Long deleteId = noticeCommentsService.delete(requestDto);
        Message message = Message.builder()
                .data(deleteId)
                .apiStatus(new ApiStatus(StatusEnum.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @PutMapping("/comments")
    public ResponseEntity<Message> update(@RequestBody NoticeCommentsUpdateDto requestDto) {
        Long updateId = noticeCommentsService.update(requestDto);
        Message message = Message.builder()
                .data(updateId)
                .apiStatus(new ApiStatus(StatusEnum.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }
}
