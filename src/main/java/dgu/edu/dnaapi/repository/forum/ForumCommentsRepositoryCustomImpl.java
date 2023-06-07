package dgu.edu.dnaapi.repository.forum;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dgu.edu.dnaapi.domain.ForumComments;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static dgu.edu.dnaapi.domain.QForumComments.*;
import static dgu.edu.dnaapi.domain.QForums.*;
import static dgu.edu.dnaapi.domain.QUser.*;

public class ForumCommentsRepositoryCustomImpl implements ForumCommentsRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public ForumCommentsRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ForumComments> search(Long forumId, Long start, Pageable pageable) {
        return queryFactory
                .selectFrom(forumComments)
                .join(forumComments.forum, forums).fetchJoin()
                .join(forumComments.author, user).fetchJoin()
                .where(
                        forumComments.commentGroupId.isNull(),
                        forumComments.forum.forumId.eq(forumId),
                        greaterThanLastForumCommentId(start))
                .orderBy(
                        forumComments.commentId.asc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    private BooleanExpression greaterThanLastForumCommentId(Long lastForumCommentId) {
        return lastForumCommentId != null ? forumComments.commentId.gt(lastForumCommentId) : forumComments.commentId.gt(0);
    }

    @Override
    public List<ForumComments> findAllReplyComments(Long forumId, List<Long> commentsId) {
        return queryFactory
                .selectFrom(forumComments)
                .leftJoin(forumComments.parent).fetchJoin()
                .join(forumComments.forum, forums).fetchJoin()
                .join(forumComments.author, user).fetchJoin()
                .where(
                        forumComments.forum.forumId.eq(forumId),
                        forumComments.commentGroupId.in(commentsId))
                .orderBy(
                        forumComments.parent.commentId.asc(),
                        forumComments.createdDate.asc()
                )
                .fetch();
    }

    @Override
    public List<ForumComments> findAllReplyCommentsByCommentGroupId(Long forumId, Long commentGroupId) {
        return queryFactory
                .selectFrom(forumComments)
                .leftJoin(forumComments.parent).fetchJoin()
                .join(forumComments.forum, forums).fetchJoin()
                .join(forumComments.author, user).fetchJoin()
                .where(
                        isInForumCommentsGroup(forumId, commentGroupId))
                .orderBy(
                        forumComments.parent.commentId.asc(),
                        forumComments.createdDate.asc()
                )
                .fetch();
    }

    private BooleanExpression isInForumCommentsGroup(Long forumId, Long commentGroupId) {
        return forumComments.commentId.eq(commentGroupId).or(forumComments.forum.forumId.eq(forumId).and(forumComments.commentGroupId.eq(commentGroupId)));
    }

    @Override
    public List<ForumComments> findAllCommentsByForumId(Long forumId) {
        return queryFactory
                .selectFrom(forumComments)
                .leftJoin(forumComments.parent).fetchJoin()
                .join(forumComments.forum, forums).fetchJoin()
                .where(
                        forumComments.forum.forumId.eq(forumId))
                .orderBy(
                        forumComments.parent.commentId.asc().nullsFirst(),
                        forumComments.createdDate.asc()
                )
                .fetch();
    }
}
