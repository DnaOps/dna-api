package dgu.edu.dnaapi.repository.forumPost;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dgu.edu.dnaapi.domain.*;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


import static dgu.edu.dnaapi.domain.QForumPostComment.forumPostComment;
import static dgu.edu.dnaapi.domain.QForumPostCommentLike.forumPostCommentLike;
import static dgu.edu.dnaapi.domain.QUser.*;

public class ForumPostCommentLikeRepositoryCustomImpl implements ForumPostCommentLikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ForumPostCommentLikeRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public boolean isForumPostLikedByUser(User user, ForumPostComment forumPostComment) {
        return queryFactory
                .selectOne()
                .from(forumPostCommentLike)
                .where(
                        forumPostCommentLike.user.id.eq(user.getId()),
                        forumPostCommentLike.forumPostComment.forumPostCommentId.eq(forumPostComment.getForumPostCommentId()))
                .fetchFirst() != null;
    }

    @Override
    public Optional<ForumPostCommentLike> findForumPostCommentLikeByUserIdAndForumPostCommentId(Long userId, Long forumPostCommentId) {
        return Optional.ofNullable(queryFactory
                        .selectFrom(forumPostCommentLike)
                        .join(forumPostCommentLike.forumPostComment, forumPostComment).fetchJoin()
                        .join(forumPostCommentLike.user, user).fetchJoin()
                        .where(
                                forumPostCommentLike.user.id.eq(userId),
                                forumPostCommentLike.forumPostComment.forumPostCommentId.eq(forumPostCommentId))
                        .fetchOne());

    }

    @Override
    public long deleteAllForumPostCommentLikesByForumPostCommentId(Long forumPostCommentId) {
        return queryFactory
                .delete(forumPostCommentLike)
                .where(forumPostCommentLike.forumPostComment.forumPostCommentId.eq(forumPostCommentId))
                .execute();
    }

    @Override
    public Set<Long> findForumPostCommentIdsLikedByUserId(Long userId, List<Long> forumPostCommentIds) {
        return queryFactory
                .select(forumPostCommentLike.forumPostComment.forumPostCommentId)
                .from(forumPostCommentLike)
                .where(
                        forumPostCommentLike.user.id.eq(userId)
                                .and(forumPostCommentLike.forumPostComment.forumPostCommentId.in(forumPostCommentIds)))
                .fetch()
                .stream().collect(Collectors.toSet());
    }
}
