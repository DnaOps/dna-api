package dgu.edu.dnaapi.repository.forum;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dgu.edu.dnaapi.domain.*;

import javax.persistence.EntityManager;

import static dgu.edu.dnaapi.domain.QForumComments.*;
import static dgu.edu.dnaapi.domain.QForumCommentsLikes.*;
import static dgu.edu.dnaapi.domain.QUser.*;

public class ForumCommentsLikesRepositoryCustomImpl implements ForumCommentsLikesRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ForumCommentsLikesRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public boolean isForumLikedByUser(User user, ForumComments forumComments) {
        return queryFactory
                .selectOne()
                .from(forumCommentsLikes)
                .where(
                        forumCommentsLikes.user.id.eq(user.getId()),
                        forumCommentsLikes.forumComments.commentId.eq(forumComments.getCommentId()))
                .fetchFirst() != null;
    }

    @Override
    public ForumCommentsLikes findForumCommentsLikesByUserAndForumComments(Long userId, Long forumCommentsId) {
        return queryFactory
                .selectFrom(forumCommentsLikes)
                .join(forumCommentsLikes.forumComments, forumComments).fetchJoin()
                .join(forumCommentsLikes.user, user).fetchJoin()
                .where(
                        forumCommentsLikes.user.id.eq(userId),
                        forumCommentsLikes.forumComments.commentId.eq(forumCommentsId))
                .fetchOne();
    }

    @Override
    public long deleteAllForumCommentsLikesByForumCommentsId(Long forumCommentsId) {
        return queryFactory
                .delete(forumCommentsLikes)
                .where(forumCommentsLikes.forumComments.commentId.eq(forumCommentsId))
                .execute();
    }
}
