package dgu.edu.dnaapi.controller.studyPost;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.config.jwt.JwtProperties;
import dgu.edu.dnaapi.controller.dto.PostSearchCondition;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.dto.studyPost.StudyPostResponseDto;
import dgu.edu.dnaapi.domain.dto.studyPost.StudyPostSaveRequestDto;
import dgu.edu.dnaapi.domain.response.ListResponse;
import dgu.edu.dnaapi.domain.response.Message;
import dgu.edu.dnaapi.domain.response.ResponseEntity;
import dgu.edu.dnaapi.service.TokenService;
import dgu.edu.dnaapi.service.studyPost.StudyPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import static org.springframework.util.StringUtils.hasText;

@RestController
@RequiredArgsConstructor
public class StudyPostController {

    private final StudyPostService studyPostService;
    private final TokenService tokenService;

    @PostMapping("/studyPosts")
    public ResponseEntity<Message> save(
            @JwtRequired User findUser,
            @RequestBody StudyPostSaveRequestDto requestDto
    ) {
        Long savedId = studyPostService.save(requestDto.toStudyPost(findUser));
        Message message = Message.createSuccessMessage(savedId);
        return ResponseEntity.createSuccessResponseMessage(message);
    }

    @PutMapping("/studyPosts/{studyPostId}")
    public ResponseEntity<Message> update(
            @JwtRequired User user,
            @RequestBody StudyPostSaveRequestDto requestDto,
            @PathVariable("studyPostId") Long studyPostId
    ) {
        Long updateId = studyPostService.update(requestDto, studyPostId, user.getId());
        Message message = Message.createSuccessMessage(updateId);
        return ResponseEntity.createSuccessResponseMessage(message);
    }

    @GetMapping("/studyPosts")
    public ResponseEntity<Message> findAll(
            @PageableDefault(size = 13, sort = "noticeId", direction = Sort.Direction.DESC) Pageable pageable,
            PostSearchCondition condition
    ) {
        ListResponse listResponse = studyPostService.findAllStudyPostMetaDataWithCondition(condition, pageable);
        Message message = Message.createSuccessMessage(listResponse);
        return ResponseEntity.createSuccessResponseMessage(message);
    }

    @GetMapping("/studyPosts/{studyPostId}")
    public ResponseEntity<Message> findById(
            @PathVariable("studyPostId") Long studyPostId,
            @RequestHeader(JwtProperties.HEADER_STRING) String headerTokenValue
    ) {
        Long userId = hasText(headerTokenValue) ? tokenService.getUserId(headerTokenValue) : null;
        StudyPostResponseDto responseDto = studyPostService.findStudyPostWithLikedInfoByStudyPostIdAndUserId(studyPostId, userId);
        Message message = Message.createSuccessMessage(responseDto);
        return ResponseEntity.createSuccessResponseMessage(message);
    }

    @DeleteMapping("/studyPosts/{studyPostId}")
    public ResponseEntity<Message> delete(
            @JwtRequired User user,
            @PathVariable("studyPostId") Long studyPostId
    ) {
        Long deleteId = studyPostService.delete(studyPostId, user.getId());
        Message message = Message.createSuccessMessage(deleteId);
        return ResponseEntity.createSuccessResponseMessage(message);
    }
}
