package dgu.edu.dnaapi.controller.forums;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.controller.dto.CommentSearchCondition;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.dto.comments.CommentsSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.comments.CommentsUpdateRequestDto;
import dgu.edu.dnaapi.domain.response.*;
import dgu.edu.dnaapi.service.Forums.ForumCommentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/comments")
public class ForumCommentsController {

    private final ForumCommentsService forumCommentsService;

    @PostMapping("/forums")
    public ResponseEntity<Message> save(@JwtRequired User findUser,
                                        @RequestBody CommentsSaveRequestDto requestDto) {

        Long savedId = forumCommentsService.save(findUser, requestDto);
        Message message = Message.createSuccessMessage(savedId);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/forums/{id}")
    public ResponseEntity<Message> findALLForumCommentsByForumId(
                @PathVariable Long id,
                CommentSearchCondition commentSearchCondition,
                @PageableDefault(size = 3, sort = "commentId", direction = Sort.Direction.ASC) Pageable pageable
                ) {
        ListResponse result = forumCommentsService.findAllForumComments(commentSearchCondition, id, pageable);
        Message message = Message.createSuccessMessage(result);

        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }

    @DeleteMapping("/forums/{id}")
    public ResponseEntity<Message> delete(@JwtRequired User findUser,
                                          @PathVariable Long id) {
        Long deleteId = forumCommentsService.delete(findUser.getId(), id);
        Message message = Message.createSuccessMessage(deleteId);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping("/forums/{id}")
    public ResponseEntity<Message> update(@JwtRequired User findUser,
                                          @PathVariable Long id,
                                          @RequestBody CommentsUpdateRequestDto requestDto) {
        Long updateId = forumCommentsService.update(findUser.getId(), requestDto, id);
        Message message = Message.createSuccessMessage(updateId);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }
}
