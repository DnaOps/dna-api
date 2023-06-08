package dgu.edu.dnaapi.controller.notice;

import dgu.edu.dnaapi.service.notice.NoticeCommentsLikesService;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/comments/notices")
@AllArgsConstructor
public class NoticeCommentsLikesController {

    private final NoticeCommentsLikesService noticeCommentsLikesService;
}
