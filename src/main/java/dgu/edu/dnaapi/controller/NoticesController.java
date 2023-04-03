package dgu.edu.dnaapi.controller;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.domain.BoardSearchCriteria;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.dto.notices.NoticesDeleteRequestDto;
import dgu.edu.dnaapi.domain.dto.notices.NoticesResponseDto;
import dgu.edu.dnaapi.domain.dto.notices.NoticesSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.notices.NoticesUpdateRequestDto;
import dgu.edu.dnaapi.domain.response.*;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.service.NoticesService;
import dgu.edu.dnaapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static dgu.edu.dnaapi.domain.response.DnaStatusCode.INVALID_SEARCH_OPTION;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/boards")
public class NoticesController {

    private final NoticesService noticesService;
    private final UserService userService;

    @PostMapping("/notices")
    public ResponseEntity<Message> save(@JwtRequired User findUser,
                                        @RequestBody NoticesSaveRequestDto requestDto) {

        Long savedId = noticesService.save(requestDto.toEntity(findUser));
        Message message = Message.builder()
                .data(savedId)
                .apiStatus(new ApiStatus(DnaStatusCode.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @PutMapping("/notices")
    public ResponseEntity<Message> update(@JwtRequired User user,
                                          @RequestBody NoticesUpdateRequestDto requestDto) {
        Long updateId = noticesService.update(requestDto, user.getId());
        Message message = Message.builder()
                .data(updateId)
                .apiStatus(new ApiStatus(DnaStatusCode.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    /**
     * start 조회 시작 지점
     *
     */
    @GetMapping("/notices")
    public ResponseEntity<Message> findAll(@RequestParam(value = "start", defaultValue = "0") long idx,
                                           @RequestParam(value = "criteria", required = false) BoardSearchCriteria criteria,
                                           @RequestParam(value = "keyword", required = false) String keyword,
                                           @PageableDefault(size = 13, sort = "noticeId", direction = Sort.Direction.DESC) Pageable pageable) {
        System.out.println("idx = " + idx);
        System.out.println("pageable = " + pageable);
        System.out.println("criteria = " + criteria);
        System.out.println("keyword = " + keyword);

        ListResponse listResponse;

        if(criteria != null && keyword != null){
            listResponse = noticesService.getAllNoticesByCriteria(idx, criteria, keyword, pageable);
        }else if ( criteria == null && keyword == null){
            listResponse = noticesService.getAllNotices(idx, pageable);
        }else {
            throw new DNACustomException("Invalid Search Options", INVALID_SEARCH_OPTION);
        }

        Message message = Message.builder()
                .data(listResponse)
                .apiStatus(new ApiStatus(DnaStatusCode.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/notices/{id}")
    public ResponseEntity<Message> findById(@PathVariable Long id) {
        NoticesResponseDto responseDto = new NoticesResponseDto(noticesService.findById(id));

        Message message = Message.builder()
                .data(responseDto)
                .apiStatus(new ApiStatus(DnaStatusCode.OK, null))
                .build();

        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @DeleteMapping("/notices")
    public ResponseEntity<Message> delete(@JwtRequired User user,
                                          @RequestBody NoticesDeleteRequestDto requestDto) {
        Long deleteId = noticesService.delete(requestDto, user.getId());
        Message message = Message.builder()
                .data(deleteId)
                .apiStatus(new ApiStatus(DnaStatusCode.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }
}
