package dgu.edu.dnaapi.controller.studyPost;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.response.Message;
import dgu.edu.dnaapi.domain.response.ResponseEntity;
import dgu.edu.dnaapi.service.studyPost.StudyPostCommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StudyPostCommentLikeController {

    private final StudyPostCommentLikeService studyPostCommentLikeService;

    @PostMapping("/studyPosts/{studyPostId}/comments/{commentId}/like")
    public ResponseEntity<Message> likeStudyPostComment(
            @JwtRequired User user,
            @PathVariable("studyPostId")Long studyPostId,
            @PathVariable("commentId") Long commentId
    ) {
        String result = studyPostCommentLikeService.like(user, studyPostId, commentId);
        return ResponseEntity.createSuccessResponseMessage(Message.createSuccessMessage(result));
    }

    @DeleteMapping("/studyPosts/{studyPostId}/comments/{commentId}/like")
    public ResponseEntity<Message> dislikeStudyPostComment(
            @JwtRequired User user,
            @PathVariable("studyPostId")Long studyPostId,
            @PathVariable("commentId") Long commentId
    ) {
        String result = studyPostCommentLikeService.dislike(user, studyPostId, commentId);
        return ResponseEntity.createSuccessResponseMessage(Message.createSuccessMessage(result));
    }
}
