package dgu.edu.dnaapi.controller.albumPost;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.response.Message;
import dgu.edu.dnaapi.domain.response.ResponseEntity;
import dgu.edu.dnaapi.repository.albumPost.AlbumPostLikeRepository;
import dgu.edu.dnaapi.repository.albumPost.AlbumPostRepository;
import dgu.edu.dnaapi.service.albumPost.AlbumPostLikeService;
import dgu.edu.dnaapi.service.albumPost.AlbumPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlbumPostLikeController {

    private final AlbumPostLikeService albumPostLikeService;

    @PostMapping("/albumPosts/{albumPostId}/like")
    public ResponseEntity<Message> likeForumPost(@JwtRequired User user, @PathVariable("albumPostId") Long albumPostId) {
        String result = albumPostLikeService.like(user, albumPostId);
        Message message = Message.createSuccessMessage(result);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }

    @DeleteMapping("/albumPosts/{albumPostId}/like")
    public ResponseEntity<Message> dislikeForumPost(@JwtRequired User user, @PathVariable("albumPostId") Long albumPostId) {
        String result = albumPostLikeService.dislike(user, albumPostId);
        Message message = Message.createSuccessMessage(result);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }
}
