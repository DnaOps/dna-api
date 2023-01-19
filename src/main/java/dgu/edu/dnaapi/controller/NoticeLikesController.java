package dgu.edu.dnaapi.controller;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.response.ApiStatus;
import dgu.edu.dnaapi.domain.response.Message;
import dgu.edu.dnaapi.domain.response.ResponseEntity;
import dgu.edu.dnaapi.domain.response.StatusEnum;
import dgu.edu.dnaapi.service.NoticeLikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class NoticeLikesController {
    private final NoticeLikesService noticeLikesService;

    @PostMapping("/likes/notices/{noticeId}")
    public ResponseEntity<Message> toggleLike(@JwtRequired User user, @PathVariable Long noticeId) {
        String result = noticeLikesService.toggleLike(user, noticeId);

        Message message = Message.builder()
                .data(result)
                .apiStatus(new ApiStatus(StatusEnum.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }
}
