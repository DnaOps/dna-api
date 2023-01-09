package dgu.edu.dnaapi.controller;

import dgu.edu.dnaapi.domain.dto.NoticesResponseDto;
import dgu.edu.dnaapi.domain.dto.NoticesSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.NoticesUpdateRequestDto;
import dgu.edu.dnaapi.domain.response.Message;
import dgu.edu.dnaapi.service.NoticesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class NoticesController {
    private final NoticesService noticesService;

    @PostMapping("/boards/notices")
    public Long save(@RequestBody NoticesSaveRequestDto requestDto) {
        return noticesService.save(requestDto);
    }

    @PutMapping("/boards/notices")
    public Long update(@RequestBody NoticesUpdateRequestDto requestDto) {
        return noticesService.update(requestDto);
    }

    @GetMapping("/boards/notices/{id}")
    public NoticesResponseDto findById(@PathVariable Long id) {
        return noticesService.findById(id);
    }

    @DeleteMapping("/boards/notices")
    public Long delete(@RequestBody NoticesUpdateRequestDto requestDto) {
        return noticesService.delete(requestDto);
    }
}
