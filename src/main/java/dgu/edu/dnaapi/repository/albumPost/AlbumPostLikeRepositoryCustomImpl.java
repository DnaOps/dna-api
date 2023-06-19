package dgu.edu.dnaapi.repository.albumPost;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dgu.edu.dnaapi.domain.QAlbumPostLike;

import javax.persistence.EntityManager;

import static dgu.edu.dnaapi.domain.QAlbumPostLike.albumPostLike;

public class AlbumPostLikeRepositoryCustomImpl implements AlbumPostLikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public AlbumPostLikeRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public boolean findAlbumPostLikeByAlbumPostIdAndUserId(Long albumPostId, Long userId) {
        return queryFactory
                .selectOne()
                .from(albumPostLike)
                .where(
                        albumPostLike.albumPost.albumPostId.eq(albumPostId),
                        albumPostLike.user.id.eq(userId))
                .fetchFirst() != null;
    }
}
