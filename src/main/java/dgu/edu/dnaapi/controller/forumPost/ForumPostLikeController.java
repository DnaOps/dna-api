package dgu.edu.dnaapi.controller.forumPost;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.domain.ForumPost;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.response.*;
import dgu.edu.dnaapi.service.ForumPost.ForumPostLikeService;
import dgu.edu.dnaapi.service.ForumPost.ForumPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ForumPostLikeController {

    private final ForumPostLikeService forumPostLikeService;
    private final ForumPostService forumPostService;

    @GetMapping("/forumPosts/{forumPostId}/like")
    public ResponseEntity<Message> isLikedByUser(@JwtRequired User user, @PathVariable("forumPostId") Long forumPostId) {
        ForumPost forumPost = forumPostService.findById(forumPostId);
        boolean isLiked = forumPostLikeService.isForumLikedByUser(user, forumPost);
        Message message = Message.createSuccessMessage(isLiked);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/forumPosts/{forumPostId}/like")
    public ResponseEntity<Message> likeForumPost(@JwtRequired User user, @PathVariable("forumPostId") Long forumPostId) {
        String result = forumPostLikeService.like(user, forumPostId);
        Message message = Message.createSuccessMessage(result);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }

    @DeleteMapping("/forumPosts/{forumPostId}/like")
    public ResponseEntity<Message> dislikeForumPost(@JwtRequired User user, @PathVariable("forumPostId") Long forumPostId) {
        String result = forumPostLikeService.dislike(user, forumPostId);
        Message message = Message.createSuccessMessage(result);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }
}
