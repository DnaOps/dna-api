package dgu.edu.dnaapi.repository.noticePost;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dgu.edu.dnaapi.domain.*;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static dgu.edu.dnaapi.domain.QNoticePostComment.noticePostComment;
import static dgu.edu.dnaapi.domain.QNoticePostCommentLike.noticePostCommentLike;

public class NoticePostCommentLikeRepositoryCustomImpl implements NoticePostCommentLikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public NoticePostCommentLikeRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<NoticePostCommentLike> findWithNoticePostCommentByUserIdAndNoticePostCommentId(Long userId, Long noticePostCommentId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(noticePostCommentLike)
                .join(noticePostCommentLike.noticePostComment, noticePostComment).fetchJoin()
                .where(
                        noticePostCommentLike.user.id.eq(userId),
                        noticePostCommentLike.noticePostComment.noticePostCommentId.eq(noticePostCommentId))
                .fetchOne());
    }

    @Override
    public long deleteAllNoticePostCommentLikesByNoticePostCommentId(Long noticePostCommentId) {
        return queryFactory
                .delete(noticePostCommentLike)
                .where(noticePostCommentLike.noticePostComment.noticePostCommentId.eq(noticePostCommentId))
                .execute();
    }

    @Override
    public Set<Long> findNoticePostCommentIdsLikedByUserId(Long userId, List<Long> noticePostCommentIds) {
        return queryFactory
                .select(noticePostCommentLike.noticePostComment.noticePostCommentId)
                .from(noticePostCommentLike)
                .where(
                        noticePostCommentLike.user.id.eq(userId)
                                .and(noticePostCommentLike.noticePostComment.noticePostCommentId.in(noticePostCommentIds)))
                .fetch()
                .stream().collect(Collectors.toSet());
    }
}
