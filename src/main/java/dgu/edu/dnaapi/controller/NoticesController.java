package dgu.edu.dnaapi.controller;

import dgu.edu.dnaapi.domain.dto.NoticesResponseDto;
import dgu.edu.dnaapi.domain.dto.NoticesSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.NoticesUpdateRequestDto;
import dgu.edu.dnaapi.domain.response.ListResponse;
import dgu.edu.dnaapi.domain.response.Message;
import dgu.edu.dnaapi.domain.response.ResponseEntity;
import dgu.edu.dnaapi.domain.response.StatusEnum;
import dgu.edu.dnaapi.service.NoticesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class NoticesController {
    private final NoticesService noticesService;

    @PostMapping("/boards/notices")
    public ResponseEntity<Message> save(@RequestBody NoticesSaveRequestDto requestDto) {
        Long savedId = noticesService.save(requestDto);
        Message message = Message.builder()
                .data(savedId)
                .status(StatusEnum.OK)
                .message(null)
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @PutMapping("/boards/notices")
    public ResponseEntity<Message> update(@RequestBody NoticesUpdateRequestDto requestDto) {
        Long updateId = noticesService.update(requestDto);
        Message message = Message.builder()
                .data(updateId)
                .status(StatusEnum.OK)
                .message(null)
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/boards/notices/{id}")
    public ResponseEntity<Message> findById(@PathVariable Long id) {
        NoticesResponseDto responseDto = noticesService.findById(id);
        Message message = Message.builder()
                .data(responseDto)
                .status(StatusEnum.OK)
                .message(null)
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/boards/notices")
    public ResponseEntity<Message> findAll() {
        List<NoticesResponseDto> noticesList = noticesService.findAll();
        ListResponse listResponse = ListResponse.builder()
                .list(noticesList)
                .totalCount(noticesList.size())
                .build();

        Message message = Message.builder()
                .data(listResponse)
                .status(StatusEnum.OK)
                .message(null)
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @DeleteMapping("/boards/notices")
    public ResponseEntity<Message> delete(@RequestBody NoticesUpdateRequestDto requestDto) {
        Long deleteId = noticesService.delete(requestDto);
        Message message = Message.builder()
                .data(deleteId)
                .status(StatusEnum.OK)
                .message(null)
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }
}
