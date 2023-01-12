package dgu.edu.dnaapi.controller;

import dgu.edu.dnaapi.domain.dto.*;
import dgu.edu.dnaapi.domain.response.*;
import dgu.edu.dnaapi.service.NoticesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/boards")
public class NoticesController {
    private final NoticesService noticesService;

    @PostMapping("/notices")
    public ResponseEntity<Message> save(@RequestBody NoticesSaveRequestDto requestDto) {
        Long savedId = noticesService.save(requestDto);
        Message message = Message.builder()
                .data(savedId)
                .apiStatus(new ApiStatus(StatusEnum.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @PutMapping("/notices")
    public ResponseEntity<Message> update(@RequestBody NoticesUpdateRequestDto requestDto) {
        Long updateId = noticesService.update(requestDto);
        Message message = Message.builder()
                .data(updateId)
                .apiStatus(new ApiStatus(StatusEnum.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/notices")
    public ResponseEntity<Message> findAll() {
        List<NoticesResponseDto> noticesList = noticesService.findAll();
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

    @GetMapping("/notices/{id}")
    public ResponseEntity<Message> findById(@PathVariable Long id) {
        NoticesResponseDto responseDto = noticesService.findById(id);

        Message message = Message.builder()
                .data(responseDto)
                .apiStatus(new ApiStatus(StatusEnum.OK, null))
                .build();

        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @DeleteMapping("/notices")
    public ResponseEntity<Message> delete(@RequestBody NoticesDeleteRequestDto requestDto) {
        Long deleteId = noticesService.delete(requestDto);
        Message message = Message.builder()
                .data(deleteId)
                .apiStatus(new ApiStatus(StatusEnum.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }
}
