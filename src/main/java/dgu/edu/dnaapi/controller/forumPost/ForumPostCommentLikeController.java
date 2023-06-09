package dgu.edu.dnaapi.controller.forumPost;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.response.Message;
import dgu.edu.dnaapi.domain.response.ResponseEntity;
import dgu.edu.dnaapi.service.ForumPost.ForumPostCommentLikeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class ForumPostCommentLikeController {

    private final ForumPostCommentLikeService forumPostCommentLikeService;

    @PostMapping("/forumPosts/{forumPostId}/comments/{commentId}/like")
    public ResponseEntity<Message> likeForumPostComment(@JwtRequired User user,@PathVariable("forumPostId")Long forumPostId, @PathVariable("commentId") Long commentId) {
        String result = forumPostCommentLikeService.like(user, forumPostId, commentId);
        Message message = Message.createSuccessMessage(result);
        return new ResponseEntity<>(message, new HttpHeaders(), HttpStatus.OK);
    }

    @DeleteMapping("/forumPosts/{forumPostId}/comments/{commentId}/like")
    public ResponseEntity<Message> dislikeForumPostComment(@JwtRequired User user,@PathVariable("forumPostId")Long forumPostId, @PathVariable("commentId") Long commentId) {
        String result = forumPostCommentLikeService.dislike(user, forumPostId, commentId);
        Message message = Message.createSuccessMessage(result);
        return new ResponseEntity<>(message, new HttpHeaders(), HttpStatus.OK);
    }
}
