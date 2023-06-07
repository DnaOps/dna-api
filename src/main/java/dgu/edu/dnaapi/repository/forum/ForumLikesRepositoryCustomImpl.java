package dgu.edu.dnaapi.repository.forum;

import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import static dgu.edu.dnaapi.domain.QForumLikes.*;

public class ForumLikesRepositoryCustomImpl implements ForumLikesRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public ForumLikesRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public boolean findForumLikesByUserIdAndForumId(Long forumId, Long userId) {
        return queryFactory
                .selectOne()
                .from(forumLikes)
                .where(
                      forumLikes.forum.forumId.eq(forumId),
                      forumLikes.user.id.eq(userId))
                .fetchFirst() != null;
    }
}
