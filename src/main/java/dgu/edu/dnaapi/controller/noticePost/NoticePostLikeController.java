package dgu.edu.dnaapi.controller.noticePost;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.response.*;
import dgu.edu.dnaapi.service.noticePost.NoticePostLikeService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NoticePostLikeController {
    private final NoticePostLikeService noticePostLikeService;

    @PostMapping("/noticePosts/{noticePostId}/like")
    public ResponseEntity<Message> likeForumPost(
            @JwtRequired User user,
            @PathVariable("noticePostId") Long noticePostId
    ) {
        String result = noticePostLikeService.like(user, noticePostId);
        Message message = Message.createSuccessMessage(result);
        return ResponseEntity.createSuccessResponseMessage(message);
    }

    @DeleteMapping("/noticePosts/{noticePostId}/like")
    public ResponseEntity<Message> dislikeForumPost(
            @JwtRequired User user,
            @PathVariable("noticePostId") Long noticePostId
    ) {
        String result = noticePostLikeService.dislike(user, noticePostId);
        Message message = Message.createSuccessMessage(result);
        return ResponseEntity.createSuccessResponseMessage(message);
    }
}
