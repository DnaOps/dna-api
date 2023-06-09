package dgu.edu.dnaapi.repository.noticePost;

import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import static dgu.edu.dnaapi.domain.QNoticePostLike.noticePostLike;

public class NoticePostLikeRepositoryCustomImpl implements NoticePostLikeRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public NoticePostLikeRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public boolean findNoticePostLikeByNoticePostIdAndUserId(Long noticePostId, Long userId) {
        return queryFactory
                .selectOne()
                .from(noticePostLike)
                .where(
                        noticePostLike.noticePost.noticePostId.eq(noticePostId),
                        noticePostLike.user.id.eq(userId))
                .fetchFirst() != null;
    }
}
