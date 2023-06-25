package dgu.edu.dnaapi.repository.albumPost;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dgu.edu.dnaapi.domain.AlbumPostComment;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static dgu.edu.dnaapi.domain.QAlbumPost.albumPost;
import static dgu.edu.dnaapi.domain.QAlbumPostComment.albumPostComment;
import static dgu.edu.dnaapi.domain.QUser.user;

public class AlbumPostCommentRepositoryCustomImpl implements AlbumPostCommentRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public AlbumPostCommentRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<AlbumPostComment> search(Long albumPostId, Long start, Pageable pageable) {
        return queryFactory
                .selectFrom(albumPostComment)
                .join(albumPostComment.albumPost, albumPost).fetchJoin()
                .join(albumPostComment.author, user).fetchJoin()
                .where(
                        albumPostComment.commentGroupId.isNull(),
                        albumPostComment.albumPost.albumPostId.eq(albumPostId),
                        greaterThanLastAlbumPostCommentId(start))
                .orderBy(
                        albumPostComment.albumPostCommentId.asc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    private BooleanExpression greaterThanLastAlbumPostCommentId(Long lastAlbumPostCommentId) {
        return lastAlbumPostCommentId != null ? albumPostComment.albumPostCommentId.gt(lastAlbumPostCommentId) : albumPostComment.albumPostCommentId.gt(0);
    }

    @Override
    public List<AlbumPostComment> findAllReplyAlbumPostComments(Long albumPostId, List<Long> commentsId) {
        return queryFactory
                .selectFrom(albumPostComment)
                .leftJoin(albumPostComment.parent).fetchJoin()
                .join(albumPostComment.albumPost, albumPost).fetchJoin()
                .join(albumPostComment.author, user).fetchJoin()
                .where(
                        albumPostComment.albumPost.albumPostId.eq(albumPostId),
                        albumPostComment.commentGroupId.in(commentsId))
                .orderBy(
                        albumPostComment.parent.albumPostCommentId.asc(),
                        albumPostComment.createdDate.asc())
                .fetch();
    }

    @Override
    public List<AlbumPostComment> findAllReplyAlbumPostCommentsByCommentGroupId(Long albumPostId, Long commentGroupId) {
        return queryFactory
                .selectFrom(albumPostComment)
                .leftJoin(albumPostComment.parent).fetchJoin()
                .join(albumPostComment.albumPost, albumPost).fetchJoin()
                .join(albumPostComment.author, user).fetchJoin()
                .where(
                        isInAlbumPostCommentsGroup(albumPostId, commentGroupId))
                .orderBy(
                        albumPostComment.parent.albumPostCommentId.asc(),
                        albumPostComment.createdDate.asc())
                .fetch();
    }

    private BooleanExpression isInAlbumPostCommentsGroup(Long albumPostId, Long commentGroupId) {
        return albumPostComment.albumPostCommentId.eq(commentGroupId)
                .or(albumPostComment.albumPost.albumPostId.eq(albumPostId).and(albumPostComment.commentGroupId.eq(commentGroupId)));
    }

    @Override
    public List<AlbumPostComment> findAllAlbumPostCommentsByAlbumPostId(Long albumPostId) {
        return queryFactory
                .selectFrom(albumPostComment)
                .leftJoin(albumPostComment.parent).fetchJoin()
                .join(albumPostComment.albumPost, albumPost).fetchJoin()
                .where(
                        albumPostComment.albumPost.albumPostId.eq(albumPostId))
                .orderBy(
                        albumPostComment.parent.albumPostCommentId.asc().nullsFirst(),
                        albumPostComment.createdDate.asc()
                )
                .fetch();
    }

    @Override
    public Long getAlbumPostCommentCountByUserId(Long userId) {
        return queryFactory
                .select(albumPostComment.count())
                .from(albumPostComment)
                .where(albumPostComment.author.id.eq(userId))
                .fetchOne();
    }
}
