package dgu.edu.dnaapi.controller.noticePost;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.response.Message;
import dgu.edu.dnaapi.domain.response.ResponseEntity;
import dgu.edu.dnaapi.service.noticePost.NoticePostCommentLikeService;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class NoticePostCommentLikeController {

    private final NoticePostCommentLikeService noticePostCommentLikeService;

    @PostMapping("/noticePosts/{noticePostId}/comments/{commentId}/like")
    public ResponseEntity<Message> likeNoticePostComment(
            @JwtRequired User user,
            @PathVariable("noticePostId")Long noticePostId,
            @PathVariable("commentId") Long commentId
    ) {
        String result = noticePostCommentLikeService.like(user, noticePostId, commentId);
        return ResponseEntity.createSuccessResponseMessage(Message.createSuccessMessage(result));
    }

    @DeleteMapping("/noticePosts/{noticePostId}/comments/{commentId}/like")
    public ResponseEntity<Message> dislikeNoticePostComment(
            @JwtRequired User user,
            @PathVariable("noticePostId")Long noticePostId,
            @PathVariable("commentId") Long commentId
    ) {
        String result = noticePostCommentLikeService.dislike(user, noticePostId, commentId);
        return ResponseEntity.createSuccessResponseMessage(Message.createSuccessMessage(result));
    }
}
