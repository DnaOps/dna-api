package dgu.edu.dnaapi.controller;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.domain.Forums;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.response.*;
import dgu.edu.dnaapi.service.ForumLikesService;
import dgu.edu.dnaapi.service.ForumsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/likes")
public class ForumLikesController {

    private final ForumLikesService forumLikesService;
    private final ForumsService forumsService;

    @GetMapping("/forums/{forumId}")
    public ResponseEntity<Message> isLikedByUser(@JwtRequired User user, @PathVariable Long forumId) {
        Forums forum = forumsService.findById(forumId);
        boolean isLiked = forumLikesService.isForumLikedByUser(user, forum);

        Message message = Message.builder()
                .data(isLiked)
                .apiStatus(new ApiStatus(DnaStatusCode.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    // todo 자기가 쓴 글은 좋아요 못하도록
    @PostMapping("/forums/{forumId}")
    public ResponseEntity<Message> clickLike(@JwtRequired User user, @PathVariable Long forumId) {
        String result = forumLikesService.chageLikeStatus(user, forumId);

        Message message = Message.builder()
                .data(result)
                .apiStatus(new ApiStatus(DnaStatusCode.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }
}
