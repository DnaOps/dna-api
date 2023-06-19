package dgu.edu.dnaapi.controller.albumPost;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.config.jwt.JwtProperties;
import dgu.edu.dnaapi.controller.dto.CommentSearchCondition;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.dto.comment.CommentSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.comment.CommentUpdateRequestDto;
import dgu.edu.dnaapi.domain.response.ListResponse;
import dgu.edu.dnaapi.domain.response.Message;
import dgu.edu.dnaapi.domain.response.ResponseEntity;
import dgu.edu.dnaapi.service.TokenService;
import dgu.edu.dnaapi.service.albumPost.AlbumPostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.util.StringUtils.hasText;

@RestController
@RequiredArgsConstructor
public class AlbumPostCommentController {

    private final AlbumPostCommentService albumPostCommentService;
    private final TokenService tokenService;


    @PostMapping("/albumPosts/{albumPostId}/comments")
    public ResponseEntity<Message> save(@JwtRequired User findUser,
                                        @PathVariable("albumPostId") Long albumPostId,
                                        @RequestBody CommentSaveRequestDto requestDto) {
        Long savedId = albumPostCommentService.save(findUser, requestDto, albumPostId);
        Message message = Message.createSuccessMessage(savedId);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/albumPosts/{albumPostId}/comments")
    public ResponseEntity<Message> findALLAlbumPostCommentsByAlbumPostId(
            @PathVariable("albumPostId") Long albumPostId,
            CommentSearchCondition commentSearchCondition,
            @PageableDefault(size = 7, sort = "commentId", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestHeader(JwtProperties.HEADER_STRING) String headerTokenValue
    ) {
        if(hasText(headerTokenValue))
            commentSearchCondition.setUserId(tokenService.getUserId(headerTokenValue));
        ListResponse result = albumPostCommentService.findAllAlbumComments(commentSearchCondition, albumPostId, pageable);
        Message message = Message.createSuccessMessage(result);

        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }

    @DeleteMapping("/albumPosts/{albumPostId}/comments/{commentId}")
    public ResponseEntity<Message> delete(@JwtRequired User findUser,
                                          @PathVariable("albumPostId") Long albumPostId,
                                          @PathVariable("commentId") Long commentId) {
        Long deleteId = albumPostCommentService.delete(findUser.getId(), commentId, albumPostId);
        Message message = Message.createSuccessMessage(deleteId);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping("/albumPosts/{albumPostId}/comments/{commentId}")
    public ResponseEntity<Message> update(@JwtRequired User findUser,
                                          @PathVariable("albumPostId") Long albumPostId,
                                          @PathVariable("commentId") Long commentId,
                                          @RequestBody CommentUpdateRequestDto requestDto) {
        Long updateId = albumPostCommentService.update(findUser.getId(), requestDto, commentId, albumPostId);
        Message message = Message.createSuccessMessage(updateId);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }
}
