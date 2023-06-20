package dgu.edu.dnaapi.controller.studyPost;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.config.jwt.JwtProperties;
import dgu.edu.dnaapi.controller.dto.CommentSearchCondition;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.dto.comment.CommentSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.comment.CommentUpdateRequestDto;
import dgu.edu.dnaapi.domain.response.ListResponse;
import dgu.edu.dnaapi.domain.response.Message;
import dgu.edu.dnaapi.domain.response.ResponseEntity;
import dgu.edu.dnaapi.service.TokenService;
import dgu.edu.dnaapi.service.studyPost.StudyPostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import static org.springframework.util.StringUtils.hasText;

@RestController
@RequiredArgsConstructor
public class StudyPostCommentController {

    private final StudyPostCommentService studyPostCommentService;
    private final TokenService tokenService;

    @PostMapping("/studyPosts/{studyPostId}/comments")
    public ResponseEntity<Message> save(
            @JwtRequired User findUser,
            @PathVariable("studyPostId") Long studyPostId,
            @RequestBody CommentSaveRequestDto requestDto
    ) {
        Long savedId = studyPostCommentService.save(findUser, requestDto, studyPostId);
        Message message = Message.createSuccessMessage(savedId);
        return ResponseEntity.createSuccessResponseMessage(message);
    }

    @PutMapping("/studyPosts/{studyPostId}/comments/{commentId}")
    public ResponseEntity<Message> update(
            @JwtRequired User findUser,
            @PathVariable("studyPostId") Long studyPostId,
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentUpdateRequestDto requestDto) {
        Long updateId = studyPostCommentService.update(findUser.getId(), requestDto, commentId, studyPostId);
        return ResponseEntity.createSuccessResponseMessage(Message.createSuccessMessage(updateId));
    }

    @GetMapping("/studyPosts/{studyPostId}/comments")
    public ResponseEntity<Message> findAllByStudyPostId(
            @PathVariable("studyPostId") Long studyPostId,
            @PageableDefault(size = 7, sort = "commentId", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestHeader(JwtProperties.HEADER_STRING) String headerTokenValue,
            CommentSearchCondition commentSearchCondition
    ) {
        if(hasText(headerTokenValue))
            commentSearchCondition.setUserId(tokenService.getUserId(headerTokenValue));
        ListResponse studyPostCommentListResponse = studyPostCommentService.findAllStudyPostCommentsByCommentSearchCondition(commentSearchCondition, studyPostId, pageable);
        return ResponseEntity.createSuccessResponseMessage(Message.createSuccessMessage(studyPostCommentListResponse));
    }

    @DeleteMapping("/studyPosts/{studyPostId}/comments/{commentId}")
    public ResponseEntity<Message> delete(
            @JwtRequired User findUser,
            @PathVariable("studyPostId") Long studyPostId,
            @PathVariable("commentId") Long commentId
    ) {
        Long deleteId = studyPostCommentService.delete(findUser.getId(), commentId, studyPostId);
        return ResponseEntity.createSuccessResponseMessage(Message.createSuccessMessage(deleteId));
    }
}
