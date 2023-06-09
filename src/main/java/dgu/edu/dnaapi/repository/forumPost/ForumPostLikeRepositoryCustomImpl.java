package dgu.edu.dnaapi.repository.forumPost;

import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import static dgu.edu.dnaapi.domain.QForumPostLike.forumPostLike;


public class ForumPostLikeRepositoryCustomImpl implements ForumPostLikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ForumPostLikeRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public boolean findForumPostLikeByForumPostIdAndUserId(Long forumPostId, Long userId) {
        return queryFactory
                .selectOne()
                .from(forumPostLike)
                .where(
                        forumPostLike.forumPost.forumPostId.eq(forumPostId),
                        forumPostLike.user.id.eq(userId))
                .fetchFirst() != null;
    }
}
