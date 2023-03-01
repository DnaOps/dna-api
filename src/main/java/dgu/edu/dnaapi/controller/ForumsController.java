package dgu.edu.dnaapi.controller;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.dto.forum.ForumSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.forum.ForumsMetaDataResponseDto;
import dgu.edu.dnaapi.domain.dto.forum.ForumsResponseDto;
import dgu.edu.dnaapi.domain.response.*;
import dgu.edu.dnaapi.service.ForumsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/boards")
public class ForumsController {

    private final ForumsService forumsService;

    @PostMapping("/forums")
    public ResponseEntity<Message> save(@JwtRequired User findUser,
                                        @RequestBody ForumSaveRequestDto requestDto) {

        Long savedId = forumsService.save(requestDto.toEntity(findUser));
        Message message = Message.builder()
                .data(savedId)
                .apiStatus(new ApiStatus(StatusEnum.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @PutMapping("/forums/{id}")
    public ResponseEntity<Message> update(@PathVariable Long id,
                                          @JwtRequired User user,
                                          @RequestBody ForumSaveRequestDto requestDto) {
        Long updateId = forumsService.update(requestDto, user.getId(), id);
        Message message = Message.builder()
                .data(updateId)
                .apiStatus(new ApiStatus(StatusEnum.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/forums")
    public ResponseEntity<Message> findAll() {
        List<ForumsMetaDataResponseDto> noticesList = forumsService.findAllForumMetaData();
        ListResponse listResponse = ListResponse.builder()
                .list(noticesList)
                .totalCount(noticesList.size())
                .build();

        Message message = Message.builder()
                .data(listResponse)
                .apiStatus(new ApiStatus(StatusEnum.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/forums/{id}")
    public ResponseEntity<Message> findById(@PathVariable Long id) {
        ForumsResponseDto responseDto = new ForumsResponseDto(forumsService.findById(id));
        Message message = Message.builder()
                .data(responseDto)
                .apiStatus(new ApiStatus(StatusEnum.OK, null))
                .build();

        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @DeleteMapping("/forums/{id}")
    public ResponseEntity<Message> delete(@JwtRequired User user,
                                          @PathVariable Long id) {
        Long deleteId = forumsService.delete(id, user.getId());
        Message message = Message.builder()
                .data(deleteId)
                .apiStatus(new ApiStatus(StatusEnum.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }
}
