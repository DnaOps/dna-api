package dgu.edu.dnaapi.controller.studyPost;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.response.Message;
import dgu.edu.dnaapi.domain.response.ResponseEntity;
import dgu.edu.dnaapi.service.studyPost.StudyPostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StudyPostLikeController {

    private final StudyPostLikeService studyPostLikeService;

    @PostMapping("/studyPosts/{studyPostId}/like")
    public ResponseEntity<Message> likeForumPost(
            @JwtRequired User user,
            @PathVariable("studyPostId") Long studyPostId
    ) {
        String result = studyPostLikeService.like(user, studyPostId);
        Message message = Message.createSuccessMessage(result);
        return ResponseEntity.createSuccessResponseMessage(message);
    }

    @DeleteMapping("/studyPosts/{studyPostId}/like")
    public ResponseEntity<Message> dislikeForumPost(
            @JwtRequired User user,
            @PathVariable("studyPostId") Long studyPostId
    ) {
        String result = studyPostLikeService.dislike(user, studyPostId);
        Message message = Message.createSuccessMessage(result);
        return ResponseEntity.createSuccessResponseMessage(message);
    }
}
