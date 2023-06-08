package dgu.edu.dnaapi.controller.notice;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.domain.Notices;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.response.*;
import dgu.edu.dnaapi.service.notice.NoticeLikesService;
import dgu.edu.dnaapi.service.notice.NoticesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class NoticeLikesController {
    private final NoticeLikesService noticeLikesService;
    private final NoticesService noticesService;

    @GetMapping("/likes/notices/{noticeId}")
    public ResponseEntity<Message> isLikedByUser(@JwtRequired User user, @PathVariable Long noticeId) {
        Notices notice = noticesService.findById(noticeId);
        boolean isLiked = noticeLikesService.isNoticeLikedByUser(user, notice);

        Message message = Message.builder()
                .data(isLiked)
                .apiStatus(new ApiStatus(DnaStatusCode.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    // todo 자기가 쓴 글은 좋아요 못하도록
    @PostMapping("/likes/notices/{noticeId}")
    public ResponseEntity<Message> toggleLike(@JwtRequired User user, @PathVariable Long noticeId) {
        String result = noticeLikesService.toggleLike(user, noticeId);

        Message message = Message.builder()
                .data(result)
                .apiStatus(new ApiStatus(DnaStatusCode.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }
}
