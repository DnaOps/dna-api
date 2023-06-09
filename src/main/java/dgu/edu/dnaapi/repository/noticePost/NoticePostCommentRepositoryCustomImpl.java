package dgu.edu.dnaapi.repository.noticePost;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dgu.edu.dnaapi.domain.NoticePostComment;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static dgu.edu.dnaapi.domain.QNoticePost.noticePost;
import static dgu.edu.dnaapi.domain.QNoticePostComment.noticePostComment;
import static dgu.edu.dnaapi.domain.QUser.user;

public class NoticePostCommentRepositoryCustomImpl implements NoticePostCommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public NoticePostCommentRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<NoticePostComment> search(Long noticePostId, Long start, Pageable pageable) {
        return queryFactory
                .selectFrom(noticePostComment)
                .join(noticePostComment.noticePost, noticePost).fetchJoin()
                .join(noticePostComment.author, user).fetchJoin()
                .where(
                        noticePostComment.commentGroupId.isNull(),
                        noticePostComment.noticePost.noticePostId.eq(noticePostId),
                        greaterThanLastNoticePostCommentId(start))
                .orderBy(
                        noticePostComment.noticePostCommentId.asc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    private BooleanExpression greaterThanLastNoticePostCommentId(Long lastNoticePostCommentId) {
        return lastNoticePostCommentId != null ? noticePostComment.noticePostCommentId.gt(lastNoticePostCommentId) : noticePostComment.noticePostCommentId.gt(0);
    }

    @Override
    public List<NoticePostComment> findAllReplyNoticePostComments(Long noticePostId, List<Long> commentIds) {
        return queryFactory
                .selectFrom(noticePostComment)
                .leftJoin(noticePostComment.parent).fetchJoin()
                .join(noticePostComment.noticePost, noticePost).fetchJoin()
                .join(noticePostComment.author, user).fetchJoin()
                .where(
                        noticePostComment.noticePost.noticePostId.eq(noticePostId),
                        noticePostComment.commentGroupId.in(commentIds))
                .orderBy(
                        noticePostComment.parent.noticePostCommentId.asc(),
                        noticePostComment.createdDate.asc())
                .fetch();
    }

    @Override
    public List<NoticePostComment> findAllReplyNoticePostCommentsByCommentGroupId(Long noticePostId, Long commentGroupId) {
        return queryFactory
                .selectFrom(noticePostComment)
                .leftJoin(noticePostComment.parent).fetchJoin()
                .join(noticePostComment.noticePost, noticePost).fetchJoin()
                .join(noticePostComment.author, user).fetchJoin()
                .where(
                        isInNoticePostCommentsGroup(noticePostId, commentGroupId))
                .orderBy(
                        noticePostComment.parent.noticePostCommentId.asc(),
                        noticePostComment.createdDate.asc()
                )
                .fetch();
    }

    private BooleanExpression isInNoticePostCommentsGroup(Long noticePostId, Long commentGroupId) {
        return noticePostComment.noticePostCommentId.eq(commentGroupId)
                .or(noticePostComment.noticePost.noticePostId.eq(noticePostId).and(noticePostComment.commentGroupId.eq(commentGroupId)));
    }

    @Override
    public List<NoticePostComment> findAllNoticePostCommentsWithNoticePostByNoticePostId(Long noticePostId) {
        return queryFactory
                .selectFrom(noticePostComment)
                .leftJoin(noticePostComment.parent).fetchJoin()
                .join(noticePostComment.noticePost, noticePost).fetchJoin()
                .where(
                        noticePostComment.noticePost.noticePostId.eq(noticePostId))
                .orderBy(
                        noticePostComment.parent.noticePostCommentId.asc().nullsFirst(),
                        noticePostComment.createdDate.asc())
                .fetch();
    }
}
