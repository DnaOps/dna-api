package dgu.edu.dnaapi.service;

import dgu.edu.dnaapi.domain.*;
import dgu.edu.dnaapi.repository.forum.ForumLikesRepository;
import dgu.edu.dnaapi.repository.forum.ForumsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ForumLikesService {

    private final ForumsRepository forumsRepository;
    private final ForumLikesRepository forumLikesRepository;

    @Transactional
    public String changeLikeStatus(User user, Long forumId) {
        Forums forum = forumsRepository.findById(forumId).orElseThrow();
        ForumLikes likes = forumLikesRepository.findByUserAndForum(user, forum);

        if(likes != null) {
            forumsRepository.decreaseLikeCount(forum.getForumId());
            forumLikesRepository.delete(likes);
            return "deleted";
        }
        forumsRepository.increaseLikeCount(forum.getForumId());
        forumLikesRepository.save(new ForumLikes(forum, user));
        return "added";
    }

    public boolean isForumLikedByUser(User user, Forums forum) {
        return forumLikesRepository.findByUserAndForum(user, forum) != null;
    }
}
