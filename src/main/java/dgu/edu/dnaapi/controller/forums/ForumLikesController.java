package dgu.edu.dnaapi.controller.forums;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.domain.Forums;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.response.*;
import dgu.edu.dnaapi.service.Forums.ForumLikesService;
import dgu.edu.dnaapi.service.Forums.ForumsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/likes")
public class ForumLikesController {

    private final ForumLikesService forumLikesService;
    private final ForumsService forumsService;

    @GetMapping("/forums/{forumId}")
    public ResponseEntity<Message> isLikedByUser(@JwtRequired User user, @PathVariable Long forumId) {
        Forums forum = forumsService.findById(forumId);
        boolean isLiked = forumLikesService.isForumLikedByUser(user, forum);
        Message message = Message.createSuccessMessage(isLiked);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/forums/{forumId}")
    public ResponseEntity<Message> clickLike(@JwtRequired User user, @PathVariable Long forumId) {
        String result = forumLikesService.changeLikeStatus(user, forumId);
        Message message = Message.createSuccessMessage(result);
        return new ResponseEntity(message, new HttpHeaders(), HttpStatus.OK);
    }
}
