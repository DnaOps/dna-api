package dgu.edu.dnaapi.controller.forumPost;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.config.jwt.JwtProperties;
import dgu.edu.dnaapi.controller.dto.CommentSearchCondition;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.dto.comment.CommentSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.comment.CommentUpdateRequestDto;
import dgu.edu.dnaapi.domain.response.*;
import dgu.edu.dnaapi.service.ForumPost.ForumPostCommentService;
import dgu.edu.dnaapi.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.util.StringUtils.hasText;

@RestController
@RequiredArgsConstructor
public class ForumPostCommentController {

    private final ForumPostCommentService forumPostCommentService;
    private final TokenService tokenService;

    @PostMapping("/forumPosts/{forumPostId}/comments")
    public ResponseEntity<Message> save(@JwtRequired User findUser,
                                        @PathVariable("forumPostId") Long forumPostId,
                                        @RequestBody CommentSaveRequestDto requestDto) {
        Long savedId = forumPostCommentService.save(findUser, requestDto, forumPostId);
        Message message = Message.createSuccessMessage(savedId);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/forumPosts/{forumPostId}/comments")
    public ResponseEntity<Message> findALLForumCommentsByForumId(
                @PathVariable("forumPostId") Long forumPostId,
                CommentSearchCondition commentSearchCondition,
                @PageableDefault(size = 7, sort = "commentId", direction = Sort.Direction.ASC) Pageable pageable,
                @RequestHeader(JwtProperties.HEADER_STRING) String headerTokenValue
                ) {
        if(hasText(headerTokenValue))
            commentSearchCondition.setUserId(tokenService.getUserId(headerTokenValue));
        ListResponse result = forumPostCommentService.findAllForumComments(commentSearchCondition, forumPostId, pageable);
        Message message = Message.createSuccessMessage(result);

        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }

    @DeleteMapping("/forumPosts/{forumPostId}/comments/{commentId}")
    public ResponseEntity<Message> delete(@JwtRequired User findUser,
                                          @PathVariable("forumPostId") Long forumPostId,
                                          @PathVariable("commentId") Long commentId) {
        Long deleteId = forumPostCommentService.delete(findUser.getId(), commentId, forumPostId);
        Message message = Message.createSuccessMessage(deleteId);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping("/forumPosts/{forumPostId}/comments/{commentId}")
    public ResponseEntity<Message> update(@JwtRequired User findUser,
                                          @PathVariable("forumPostId") Long forumPostId,
                                          @PathVariable("commentId") Long commentId,
                                          @RequestBody CommentUpdateRequestDto requestDto) {
        Long updateId = forumPostCommentService.update(findUser.getId(), requestDto, commentId, forumPostId);
        Message message = Message.createSuccessMessage(updateId);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }
}
