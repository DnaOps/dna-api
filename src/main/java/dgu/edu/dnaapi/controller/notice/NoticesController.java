package dgu.edu.dnaapi.controller.notice;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.config.jwt.JwtProperties;
import dgu.edu.dnaapi.controller.dto.BoardSearchCondition;
import dgu.edu.dnaapi.domain.BoardSearchCriteria;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.dto.notices.NoticesDeleteRequestDto;
import dgu.edu.dnaapi.domain.dto.notices.NoticesResponseDto;
import dgu.edu.dnaapi.domain.dto.notices.NoticesSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.notices.NoticesUpdateRequestDto;
import dgu.edu.dnaapi.domain.response.*;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.service.TokenService;
import dgu.edu.dnaapi.service.notice.NoticesService;
import dgu.edu.dnaapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static dgu.edu.dnaapi.domain.response.DnaStatusCode.INVALID_SEARCH_OPTION;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/boards")
public class NoticesController {

    private final NoticesService noticesService;
    private final TokenService tokenService;

    @PostMapping("/notices")
    public ResponseEntity<Message> save(@JwtRequired User findUser,
                                        @RequestBody NoticesSaveRequestDto requestDto) {

        Long savedId = noticesService.save(requestDto.toEntity(findUser));
        Message message = Message.createSuccessMessage(savedId);
        return ResponseEntity.createSuccessResponseMessage(message);
    }

    @PutMapping("/notices/{noticeId}")
    public ResponseEntity<Message> update(@JwtRequired User user,
                                          @RequestBody NoticesUpdateRequestDto requestDto,
                                          @PathVariable("noticeId") Long noticeId) {
        Long updateId = noticesService.update(requestDto, noticeId, user.getId());
        Message message = Message.createSuccessMessage(updateId);
        return ResponseEntity.createSuccessResponseMessage(message);
    }

    @GetMapping("/notices")
    public ResponseEntity<Message> findAll(
            @PageableDefault(size = 13, sort = "noticeId", direction = Sort.Direction.DESC) Pageable pageable,
            BoardSearchCondition condition
    ) {
        ListResponse listResponse = noticesService.findAllNoticesMetaDataWithCondition(condition, pageable);
        Message message = Message.createSuccessMessage(listResponse);
        return ResponseEntity.createSuccessResponseMessage(message);
    }

    @GetMapping("/notices/{noticeId}")
    public ResponseEntity<Message> findById(
            @PathVariable("noticeId") Long noticeId,
            @RequestHeader(JwtProperties.HEADER_STRING) String headerTokenValue
    ) {
        Long userId = hasText(headerTokenValue) ? tokenService.getUserId(headerTokenValue) : null;
        NoticesResponseDto responseDto = noticesService.findNoticeWithLikedInfoByNoticeIdAndUserId(noticeId, userId);
        Message message = Message.createSuccessMessage(responseDto);
        return ResponseEntity.createSuccessResponseMessage(message);
    }

    @DeleteMapping("/notices/{noticeId}")
    public ResponseEntity<Message> delete(@JwtRequired User user,
                                          @PathVariable("noticeId") Long noticeId) {
        Long deleteId = noticesService.delete(noticeId, user.getId());
        Message message = Message.createSuccessMessage(deleteId);
        return ResponseEntity.createSuccessResponseMessage(message);
    }
}
