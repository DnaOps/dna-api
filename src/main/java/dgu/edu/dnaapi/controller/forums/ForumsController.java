package dgu.edu.dnaapi.controller.forums;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.config.jwt.JwtProperties;
import dgu.edu.dnaapi.controller.dto.BoardSearchCondition;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.dto.forum.ForumSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.forum.ForumsResponseDto;
import dgu.edu.dnaapi.domain.response.*;
import dgu.edu.dnaapi.service.Forums.ForumsService;
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
@RequestMapping(value = "/boards")
public class ForumsController {

    private final ForumsService forumsService;
    private final TokenService tokenService;

    @PostMapping("/forums")
    public ResponseEntity<Message> save(@JwtRequired User findUser,
                                        @RequestBody ForumSaveRequestDto requestDto) {

        Long savedId = forumsService.save(requestDto.toEntity(findUser));
        Message message = Message.createSuccessMessage(savedId);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping("/forums/{id}")
    public ResponseEntity<Message> update(@PathVariable Long id,
                                          @JwtRequired User user,
                                          @RequestBody ForumSaveRequestDto requestDto) {
        Long updateId = forumsService.update(requestDto, user.getId(), id);
        Message message = Message.createSuccessMessage(updateId);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/forums")
    public ResponseEntity<Message> findAll(
            @PageableDefault(size = 13, sort = "noticeId", direction = Sort.Direction.DESC) Pageable pageable,
            BoardSearchCondition condition
    ) {
        ListResponse result = forumsService.findAllForumsMetaDataWithCondition(condition, pageable);
        return new ResponseEntity(result, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/forums/{id}")
    public ResponseEntity<Message> findById(
            @PathVariable Long id,
            @RequestHeader(JwtProperties.HEADER_STRING) String authorizationHeaderValue
    ) {
        Long userId = hasText(authorizationHeaderValue) ? tokenService.getUserId(authorizationHeaderValue) : null;
        ForumsResponseDto result = forumsService.findForumWithLikedInfoByForumIdAndUserId(id, userId);
        Message message = Message.createSuccessMessage(result);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }

    @DeleteMapping("/forums/{id}")
    public ResponseEntity<Message> delete(@JwtRequired User user,
                                          @PathVariable Long id) {
        Long deleteId = forumsService.delete(id, user.getId());
        Message message = Message.createSuccessMessage(deleteId);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }
}
