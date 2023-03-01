package dgu.edu.dnaapi.controller;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.dto.comments.CommentsSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.comments.CommentsUpdateRequestDto;
import dgu.edu.dnaapi.domain.dto.forumComments.ForumCommentsResponseDto;
import dgu.edu.dnaapi.domain.response.*;
import dgu.edu.dnaapi.service.ForumCommentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/comments")
public class ForumCommentsController {

    private final ForumCommentsService forumCommentsService;

    @PostMapping("/forums")
    public ResponseEntity<Message> save(@JwtRequired User findUser,
                                        @RequestBody CommentsSaveRequestDto requestDto) {

        Long savedId = forumCommentsService.save(findUser, requestDto);
        Message message = Message.builder()
                .data(savedId)
                .apiStatus(new ApiStatus(StatusEnum.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/forums/{id}")
    public ResponseEntity<Message> findALLForumCommentsByForumId(@PathVariable Long id) {
        List<ForumCommentsResponseDto> comments = forumCommentsService.findAllForumComments(id);

        ListResponse listResponse = ListResponse.builder()
                .list(comments)
                .totalCount(comments.size())
                .build();

        Message message = Message.builder()
                .data(listResponse)
                .apiStatus(new ApiStatus(StatusEnum.OK, null))
                .build();

        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @DeleteMapping("/forums/{id}")
    public ResponseEntity<Message> delete(@JwtRequired User findUser,
                                          @PathVariable Long id) {
        Long deleteId = forumCommentsService.delete(findUser.getId(), id);
        Message message = Message.builder()
                .data(deleteId)
                .apiStatus(new ApiStatus(StatusEnum.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @PutMapping("/forums/{id}")
    public ResponseEntity<Message> update(@JwtRequired User findUser,
                                          @PathVariable Long id,
                                          @RequestBody CommentsUpdateRequestDto requestDto) {
        Long updateId = forumCommentsService.update(findUser.getId(), requestDto, id);
        Message message = Message.builder()
                .data(updateId)
                .apiStatus(new ApiStatus(StatusEnum.OK, null))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }
}
