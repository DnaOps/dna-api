package dgu.edu.dnaapi.repository.albumPost;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dgu.edu.dnaapi.domain.*;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static dgu.edu.dnaapi.domain.QAlbumPostComment.albumPostComment;
import static dgu.edu.dnaapi.domain.QAlbumPostCommentLike.albumPostCommentLike;
import static dgu.edu.dnaapi.domain.QUser.user;

public class AlbumPostCommentLikeRepositoryCustomImpl implements AlbumPostCommentLikeRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public AlbumPostCommentLikeRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Set<Long> findAlbumPostCommentIdsLikedByUserId(Long userId, List<Long> allAlbumCommentIds) {
        return queryFactory
                .select(albumPostCommentLike.albumPostComment.albumPostCommentId)
                .from(albumPostCommentLike)
                .where(
                        albumPostCommentLike.user.id.eq(userId)
                                .and(albumPostCommentLike.albumPostComment.albumPostCommentId.in(allAlbumCommentIds)))
                .fetch()
                .stream().collect(Collectors.toSet());
    }

    @Override
    public boolean isAlbumPostLikedByUser(User user, AlbumPostComment albumPostComment) {
        return queryFactory
                .selectOne()
                .from(albumPostCommentLike)
                .where(
                        albumPostCommentLike.user.id.eq(user.getId()),
                        albumPostCommentLike.albumPostComment.albumPostCommentId.eq(albumPostComment.getAlbumPostCommentId()))
                .fetchFirst() != null;
    }

    @Override
    public Optional<AlbumPostCommentLike> findAlbumPostCommentLikeByUserIdAndAlbumPostCommentId(Long userId, Long albumPostCommentId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(albumPostCommentLike)
                .join(albumPostCommentLike.albumPostComment, albumPostComment).fetchJoin()
                .join(albumPostCommentLike.user, user).fetchJoin()
                .where(
                        albumPostCommentLike.user.id.eq(userId),
                        albumPostCommentLike.albumPostComment.albumPostCommentId.eq(albumPostCommentId))
                .fetchOne());
    }

    @Override
    public long deleteAllAlbumPostCommentLikesByAlbumPostCommentId(Long albumPostCommentId) {
        return queryFactory
                .delete(albumPostCommentLike)
                .where(albumPostCommentLike.albumPostComment.albumPostCommentId.eq(albumPostCommentId))
                .execute();
    }
}
