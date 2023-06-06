package dgu.edu.dnaapi.service;

import dgu.edu.dnaapi.repository.forum.ForumCommentsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ForumCommentsServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    ForumCommentsRepository forumCommentsRepository;

    @Test
    void save() {
    }
}