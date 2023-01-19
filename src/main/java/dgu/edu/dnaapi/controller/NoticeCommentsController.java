package dgu.edu.dnaapi.controller;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.dto.noticeComments.NoticeCommentsDeleteDto;
import dgu.edu.dnaapi.domain.dto.noticeComments.NoticeCommentsResponseDto;
import dgu.edu.dnaapi.domain.dto.noticeComments.NoticeCommentsSaveDto;
import dgu.edu.dnaapi.domain.dto.noticeComments.NoticeCommentsUpdateDto;
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

    @PostMapping("/comments")
    public ResponseEntity<Message> save(@JwtRequired User findUser,
                                        @RequestBody NoticeCommentsSaveDto requestDto) {

        Long savedId = noticeCommentsService.save(findUser, requestDto);
        Message message = Message.builder()
                .data(savedId)
                .apiStatus(new ApiStatus(StatusEnum.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

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

    @DeleteMapping("/comments")
    public ResponseEntity<Message> delete(@JwtRequired User findUser,
                                          @RequestBody NoticeCommentsDeleteDto requestDto) {
        Long deleteId = noticeCommentsService.delete(findUser.getId(), requestDto);
        Message message = Message.builder()
                .data(deleteId)
                .apiStatus(new ApiStatus(StatusEnum.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @PutMapping("/comments")
    public ResponseEntity<Message> update(@JwtRequired User findUser,
                                          @RequestBody NoticeCommentsUpdateDto requestDto) {
        Long updateId = noticeCommentsService.update(findUser.getId(), requestDto);
        Message message = Message.builder()
                .data(updateId)
                .apiStatus(new ApiStatus(StatusEnum.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }
}
