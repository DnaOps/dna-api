package dgu.edu.dnaapi.controller;

import dgu.edu.dnaapi.domain.dto.NoticesSaveRequestDto;
import dgu.edu.dnaapi.service.NoticesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class NoticesController {
    private final NoticesService noticesService;

    @PostMapping("/boards/notices")
    public Long save(@RequestBody NoticesSaveRequestDto requestDto) {
        return noticesService.save(requestDto);
    }
}
