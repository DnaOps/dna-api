package dgu.edu.dnaapi.repository.albumPost;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dgu.edu.dnaapi.domain.AlbumPost;

import javax.persistence.EntityManager;
import java.util.Optional;

import static dgu.edu.dnaapi.domain.QAlbumPost.albumPost;
import static dgu.edu.dnaapi.domain.QAlbumPostImage.albumPostImage;
import static dgu.edu.dnaapi.domain.QUser.user;

public class AlbumPostRepositoryCustomImpl implements AlbumPostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public AlbumPostRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<AlbumPost> findWithAlbumPostImagesByAlbumPostId(Long albumPostId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(albumPost)
                .leftJoin(albumPost.albumPostImages, albumPostImage).fetchJoin()
                .join(albumPost.author, user).fetchJoin()
                .where(albumPost.albumPostId.eq(albumPostId))
                .fetchOne());
    }
}
