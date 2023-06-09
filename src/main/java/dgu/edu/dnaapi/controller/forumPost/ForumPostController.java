package dgu.edu.dnaapi.controller.forumPost;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.config.jwt.JwtProperties;
import dgu.edu.dnaapi.controller.dto.PostSearchCondition;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.dto.forumPost.ForumPostSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.forumPost.ForumPostResponseDto;
import dgu.edu.dnaapi.domain.response.*;
import dgu.edu.dnaapi.service.ForumPost.ForumPostService;
import dgu.edu.dnaapi.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
@RestController
public class ForumPostController {

    private final ForumPostService forumPostService;
    private final TokenService tokenService;

    @PostMapping("/forumPosts")
    public ResponseEntity<Message> save(@JwtRequired User findUser,
                                        @RequestBody ForumPostSaveRequestDto requestDto) {
        Long savedId = forumPostService.save(requestDto.toEntity(findUser));
        Message message = Message.createSuccessMessage(savedId);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping("/forumPosts/{forumPostId}")
    public ResponseEntity<Message> update(@PathVariable("forumPostId") Long forumPostId,
                                          @JwtRequired User user,
                                          @RequestBody ForumPostSaveRequestDto requestDto) {
        Long updateId = forumPostService.update(requestDto, user.getId(), forumPostId);
        Message message = Message.createSuccessMessage(updateId);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/forumPosts")
    public ResponseEntity<Message> findAll(
            @PageableDefault(size = 13, sort = "noticeId", direction = Sort.Direction.DESC) Pageable pageable,
            PostSearchCondition condition
    ) {
        ListResponse result = forumPostService.findAllForumPostMetaDataWithCondition(condition, pageable);
        Message message = Message.createSuccessMessage(result);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/forumPosts/{forumPostId}")
    public ResponseEntity<Message> findById(
            @PathVariable("forumPostId") Long forumPostId,
            @RequestHeader(JwtProperties.HEADER_STRING) String authorizationHeaderValue
    ) {
        Long userId = hasText(authorizationHeaderValue) ? tokenService.getUserId(authorizationHeaderValue) : null;
        ForumPostResponseDto result = forumPostService.findForumPostWithLikedInfoByForumPostIdAndUserId(forumPostId, userId);
        Message message = Message.createSuccessMessage(result);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }

    @DeleteMapping("/forumPosts/{forumPostId}")
    public ResponseEntity<Message> delete(@JwtRequired User user,
                                          @PathVariable("forumPostId") Long forumPostId) {
        Long deleteId = forumPostService.delete(forumPostId, user.getId());
        Message message = Message.createSuccessMessage(deleteId);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }
}
