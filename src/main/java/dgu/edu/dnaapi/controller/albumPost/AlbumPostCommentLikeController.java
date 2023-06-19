package dgu.edu.dnaapi.controller.albumPost;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.response.Message;
import dgu.edu.dnaapi.domain.response.ResponseEntity;
import dgu.edu.dnaapi.service.albumPost.AlbumPostCommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlbumPostCommentLikeController {

    private final AlbumPostCommentLikeService albumPostCommentLikeService;

    @PostMapping("/albumPosts/{albumPostId}/comments/{commentId}/like")
    public ResponseEntity<Message> likeForumPostComment(
            @JwtRequired User user,
            @PathVariable("albumPostId")Long albumPostId,
            @PathVariable("commentId") Long commentId
    ) {
        String result = albumPostCommentLikeService.like(user, albumPostId, commentId);
        return ResponseEntity.createSuccessResponseMessage(Message.createSuccessMessage(result));
    }

    @DeleteMapping("/albumPosts/{albumPostId}/comments/{commentId}/like")
    public ResponseEntity<Message> dislikeForumPostComment(
            @JwtRequired User user,
            @PathVariable("albumPostId")Long albumPostId,
            @PathVariable("commentId") Long commentId
    ) {
        String result = albumPostCommentLikeService.dislike(user, albumPostId, commentId);
        return ResponseEntity.createSuccessResponseMessage(Message.createSuccessMessage(result));
    }
}
