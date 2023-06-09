package dgu.edu.dnaapi.controller.noticePost;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.config.jwt.JwtProperties;
import dgu.edu.dnaapi.controller.dto.PostSearchCondition;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.dto.noticePost.NoticePostResponseDto;
import dgu.edu.dnaapi.domain.dto.noticePost.NoticePostSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.noticePost.NoticePostUpdateRequestDto;
import dgu.edu.dnaapi.domain.response.*;
import dgu.edu.dnaapi.service.TokenService;
import dgu.edu.dnaapi.service.noticePost.NoticePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
@RestController
public class NoticePostController {

    private final NoticePostService noticePostService;
    private final TokenService tokenService;

    @PostMapping("/noticePosts")
    public ResponseEntity<Message> save(
            @JwtRequired User findUser,
            @RequestBody NoticePostSaveRequestDto requestDto
    ) {
        Long savedId = noticePostService.save(requestDto.toEntity(findUser));
        Message message = Message.createSuccessMessage(savedId);
        return ResponseEntity.createSuccessResponseMessage(message);
    }

    @PutMapping("/noticePosts/{noticePostId}")
    public ResponseEntity<Message> update(
            @JwtRequired User user,
            @RequestBody NoticePostUpdateRequestDto requestDto,
            @PathVariable("noticePostId") Long noticePostId
    ) {
        Long updateId = noticePostService.update(requestDto, noticePostId, user.getId());
        Message message = Message.createSuccessMessage(updateId);
        return ResponseEntity.createSuccessResponseMessage(message);
    }

    @GetMapping("/noticePosts")
    public ResponseEntity<Message> findAll(
            @PageableDefault(size = 13, sort = "noticeId", direction = Sort.Direction.DESC) Pageable pageable,
            PostSearchCondition condition
    ) {
        ListResponse listResponse = noticePostService.findAllNoticesMetaDataWithCondition(condition, pageable);
        Message message = Message.createSuccessMessage(listResponse);
        return ResponseEntity.createSuccessResponseMessage(message);
    }

    @GetMapping("/noticePosts/{noticePostId}")
    public ResponseEntity<Message> findById(
            @PathVariable("noticePostId") Long noticePostId,
            @RequestHeader(JwtProperties.HEADER_STRING) String headerTokenValue
    ) {
        Long userId = hasText(headerTokenValue) ? tokenService.getUserId(headerTokenValue) : null;
        NoticePostResponseDto responseDto = noticePostService.findNoticeWithLikedInfoByNoticeIdAndUserId(noticePostId, userId);
        Message message = Message.createSuccessMessage(responseDto);
        return ResponseEntity.createSuccessResponseMessage(message);
    }

    @DeleteMapping("/noticePosts/{noticePostId}")
    public ResponseEntity<Message> delete(
            @JwtRequired User user,
            @PathVariable("noticePostId") Long noticePostId
    ) {
        Long deleteId = noticePostService.delete(noticePostId, user.getId());
        Message message = Message.createSuccessMessage(deleteId);
        return ResponseEntity.createSuccessResponseMessage(message);
    }
}
