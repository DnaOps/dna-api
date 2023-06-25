package dgu.edu.dnaapi.repository.forumPost;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dgu.edu.dnaapi.domain.ForumPostComment;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static dgu.edu.dnaapi.domain.QForumPost.*;
import static dgu.edu.dnaapi.domain.QForumPostComment.*;
import static dgu.edu.dnaapi.domain.QUser.*;

public class ForumPostCommentRepositoryCustomImpl implements ForumPostCommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ForumPostCommentRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ForumPostComment> search(Long forumPostId, Long start, Pageable pageable) {
        return queryFactory
                .selectFrom(forumPostComment)
                .join(forumPostComment.forumPost, forumPost).fetchJoin()
                .join(forumPostComment.author, user).fetchJoin()
                .where(
                        forumPostComment.commentGroupId.isNull(),
                        forumPostComment.forumPost.forumPostId.eq(forumPostId),
                        greaterThanLastForumPostCommentId(start))
                .orderBy(
                        forumPostComment.forumPostCommentId.asc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    private BooleanExpression greaterThanLastForumPostCommentId(Long lastForumPostCommentId) {
        return lastForumPostCommentId != null ? forumPostComment.forumPostCommentId.gt(lastForumPostCommentId) : forumPostComment.forumPostCommentId.gt(0);
    }

    @Override
    public List<ForumPostComment> findAllReplyForumPostComments(Long forumPostId, List<Long> commentsId) {
        return queryFactory
                .selectFrom(forumPostComment)
                .leftJoin(forumPostComment.parent).fetchJoin()
                .join(forumPostComment.forumPost, forumPost).fetchJoin()
                .join(forumPostComment.author, user).fetchJoin()
                .where(
                        forumPostComment.forumPost.forumPostId.eq(forumPostId),
                        forumPostComment.commentGroupId.in(commentsId))
                .orderBy(
                        forumPostComment.parent.forumPostCommentId.asc(),
                        forumPostComment.createdDate.asc()
                )
                .fetch();
    }

    @Override
    public List<ForumPostComment> findAllReplyForumPostCommentsByCommentGroupId(Long forumPostId, Long commentGroupId) {
        return queryFactory
                .selectFrom(forumPostComment)
                .leftJoin(forumPostComment.parent).fetchJoin()
                .join(forumPostComment.forumPost, forumPost).fetchJoin()
                .join(forumPostComment.author, user).fetchJoin()
                .where(
                        isInForumPostCommentsGroup(forumPostId, commentGroupId))
                .orderBy(
                        forumPostComment.parent.forumPostCommentId.asc(),
                        forumPostComment.createdDate.asc()
                )
                .fetch();
    }

    private BooleanExpression isInForumPostCommentsGroup(Long forumPostId, Long commentGroupId) {
        return forumPostComment.forumPostCommentId.eq(commentGroupId).or(forumPostComment.forumPost.forumPostId.eq(forumPostId).and(forumPostComment.commentGroupId.eq(commentGroupId)));
    }

    @Override
    public List<ForumPostComment> findAllForumPostCommentsByForumPostId(Long forumPostId) {
        return queryFactory
                .selectFrom(forumPostComment)
                .leftJoin(forumPostComment.parent).fetchJoin()
                .join(forumPostComment.forumPost, forumPost).fetchJoin()
                .where(
                        forumPostComment.forumPost.forumPostId.eq(forumPostId))
                .orderBy(
                        forumPostComment.parent.forumPostCommentId.asc().nullsFirst(),
                        forumPostComment.createdDate.asc()
                )
                .fetch();
    }

    @Override
    public Long getForumPostCommentCountByUserId(Long userId) {
        return queryFactory
                .select(forumPostComment.count())
                .from(forumPostComment)
                .where(forumPostComment.author.id.eq(userId))
                .fetchOne();
    }
}
