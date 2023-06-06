package dgu.edu.dnaapi.controller.forums;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.response.Message;
import dgu.edu.dnaapi.domain.response.ResponseEntity;
import dgu.edu.dnaapi.repository.forum.ForumCommentsLikesRepository;
import dgu.edu.dnaapi.service.Forums.ForumCommentsLikesService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/comments/forums")
public class ForumCommentsLikesController {

    private final ForumCommentsLikesService forumCommentsLikesService;

    @PostMapping("/likes/{forumCommentsId}")
    public ResponseEntity<Message> chageLikeStatus(@JwtRequired User user, @PathVariable Long forumCommentsId) {
        String result = forumCommentsLikesService.changeLikeStatus(user, forumCommentsId);
        Message message = Message.createSuccessMessage(result);
        return new ResponseEntity<>(message, new HttpHeaders(), HttpStatus.OK);
    }
}
