package dgu.edu.dnaapi.service.Forums;

import dgu.edu.dnaapi.domain.ForumComments;
import dgu.edu.dnaapi.domain.ForumCommentsLikes;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.forum.ForumCommentsLikesRepository;
import dgu.edu.dnaapi.repository.forum.ForumCommentsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class ForumCommentsLikesService {

    private final ForumCommentsRepository forumCommentsRepository;
    private final ForumCommentsLikesRepository forumCommentsLikesRepository;

    public boolean isForumCommentsLikedByUser(User user, ForumComments forumComments){
        return forumCommentsLikesRepository.isForumLikedByUser(user, forumComments);
    }

    @Transactional
    public String changeLikeStatus(User user, Long forumCommentsId) {
        ForumComments forumComments = forumCommentsRepository.findById(forumCommentsId).orElseThrow(
                () -> new DNACustomException("해당 댓글이 존재하지 않습니다. id = " + forumCommentsId, DnaStatusCode.INVALID_COMMENT));
        if(forumComments.isDeleted())
            throw new DNACustomException("해당 댓글이 존재하지 않습니다. id = " + forumCommentsId, DnaStatusCode.INVALID_DELETE_COMMENT);

        ForumCommentsLikes forumCommentsLikes = forumCommentsLikesRepository.findForumCommentsLikesByUserAndForumComments(user.getId(), forumCommentsId);

        if (hasUserLikedForumComments(forumCommentsLikes)) {
            return disLikeForumComments(forumCommentsLikes);
        }
        return likeForumComments(user, forumComments);
    }

    private boolean hasUserLikedForumComments(ForumCommentsLikes forumCommentsLikes){
        return forumCommentsLikes != null;
    }

    private String disLikeForumComments(ForumCommentsLikes forumCommentsLikes) {
        forumCommentsRepository.decreaseLikeCount(forumCommentsLikes.getForumComments().getCommentId());
        forumCommentsLikesRepository.delete(forumCommentsLikes);
        return "deleted";
    }

    private String likeForumComments(User user, ForumComments forumComments) {
        forumCommentsRepository.increaseLikeCount(forumComments.getCommentId());
        forumCommentsLikesRepository.save(new ForumCommentsLikes(forumComments, user));
        return "added";
    }
}
