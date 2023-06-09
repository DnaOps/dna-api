package dgu.edu.dnaapi.controller.noticePost;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.config.jwt.JwtProperties;
import dgu.edu.dnaapi.controller.dto.CommentSearchCondition;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.dto.comment.CommentSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.comment.CommentUpdateRequestDto;
import dgu.edu.dnaapi.domain.response.*;
import dgu.edu.dnaapi.service.TokenService;
import dgu.edu.dnaapi.service.noticePost.NoticePostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
@RestController
public class NoticePostCommentController {

    private final NoticePostCommentService noticePostCommentService;
    private final TokenService tokenService;

    @PostMapping("/noticePosts/{noticePostId}/comments")
    public ResponseEntity<Message> save(
            @JwtRequired User findUser,
            @PathVariable("noticePostId") Long noticePostId,
            @RequestBody CommentSaveRequestDto requestDto
    ) {
        Long savedId = noticePostCommentService.save(findUser, requestDto, noticePostId);
        Message message = Message.createSuccessMessage(savedId);
        return ResponseEntity.createSuccessResponseMessage(message);
    }

    @PutMapping("/noticePosts/{noticePostId}/comments/{commentId}")
    public ResponseEntity<Message> update(
            @JwtRequired User findUser,
            @PathVariable("noticePostId") Long noticePostId,
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentUpdateRequestDto requestDto) {
        Long updateId = noticePostCommentService.update(findUser.getId(), requestDto, commentId, noticePostId);
        return ResponseEntity.createSuccessResponseMessage(Message.createSuccessMessage(updateId));
    }

    @GetMapping("/noticePosts/{noticePostId}/comments")
    public ResponseEntity<Message> findAllByNoticePostId(
            @PathVariable("noticePostId") Long noticePostId,
            @PageableDefault(size = 7, sort = "commentId", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestHeader(JwtProperties.HEADER_STRING) String headerTokenValue,
            CommentSearchCondition commentSearchCondition
    ) {
        if(hasText(headerTokenValue))
            commentSearchCondition.setUserId(tokenService.getUserId(headerTokenValue));
        ListResponse noticePostCommentListResponse = noticePostCommentService.findAllNoticePostCommentsByCommentSearchCondition(commentSearchCondition, noticePostId, pageable);
        return ResponseEntity.createSuccessResponseMessage(Message.createSuccessMessage(noticePostCommentListResponse));
    }

    @DeleteMapping("/noticePosts/{noticePostId}/comments/{commentId}")
    public ResponseEntity<Message> delete(
            @JwtRequired User findUser,
            @PathVariable("noticePostId") Long noticePostId,
            @PathVariable("commentId") Long commentId
    ) {
        Long deleteId = noticePostCommentService.delete(findUser.getId(), commentId, noticePostId);
        return ResponseEntity.createSuccessResponseMessage(Message.createSuccessMessage(deleteId));
    }
}
