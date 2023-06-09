package dgu.edu.dnaapi.service;

import dgu.edu.dnaapi.repository.forumPost.ForumPostCommentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@SpringBootTest
@Transactional
class ForumPostCommentServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    ForumPostCommentRepository forumPostCommentRepository;

    @Test
    void save() {
    }
}