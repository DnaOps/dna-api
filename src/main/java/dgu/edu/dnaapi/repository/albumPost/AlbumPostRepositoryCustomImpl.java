package dgu.edu.dnaapi.repository.albumPost;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dgu.edu.dnaapi.controller.dto.PostSearchCondition;
import dgu.edu.dnaapi.domain.AlbumPost;
import dgu.edu.dnaapi.domain.dto.albumPost.AlbumPostMetaDataResponseDto;
import dgu.edu.dnaapi.domain.dto.albumPost.QAlbumPostMetaDataResponseDto;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static dgu.edu.dnaapi.domain.QAlbumPost.albumPost;
import static dgu.edu.dnaapi.domain.QAlbumPostComment.albumPostComment;
import static dgu.edu.dnaapi.domain.QAlbumPostImage.albumPostImage;
import static dgu.edu.dnaapi.domain.QUser.user;
import static org.springframework.util.StringUtils.hasText;

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

    @Override
    public List<AlbumPostMetaDataResponseDto> search(PostSearchCondition condition, Pageable pageable) {
        return queryFactory
                .select(new QAlbumPostMetaDataResponseDto(
                        albumPost.albumPostId,
                        albumPost.title,
                        albumPost.author.userName.as("author"),
                        albumPost.author.id.as("authorId"),
                        albumPost.commentCount,
                        albumPost.likeCount,
                        albumPost.author.level,
                        albumPost.lastModifiedDate.as("modifiedAt"),
                        albumPost.thumbnailImage))
                .from(albumPost)
                .join(albumPost.author, user)
                .where(
                        lowerThanLastAlbumPostId(condition.getStart()),
                        containKeyword(condition.getTitle(), condition.getContent()),
                        authorNameEq(condition.getAuthor()))
                .orderBy(albumPost.albumPostId.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    private BooleanExpression lowerThanLastAlbumPostId(Long lastAlbumPostId) {
        return lastAlbumPostId != null ? albumPost.albumPostId.lt(lastAlbumPostId) : albumPost.albumPostId.lt(Long.MAX_VALUE);
    }

    private BooleanExpression authorNameEq(String author) {
        return hasText(author) ? albumPost.author.userName.eq(author) : null;
    }

    private BooleanExpression containKeyword(String titleKeyword, String contentKeyword){
        boolean titleCondition = hasText(titleKeyword);
        boolean contentCondition = hasText(contentKeyword);

        if(titleCondition && contentCondition){
            return containKeywordInTitleOrContent(titleKeyword);
        }else if(titleCondition){
            return containKeywordInTitle(titleKeyword);
        }else if(contentCondition) {
            return containKeywordInContent(contentKeyword);
        }
        return null;
    }

    private BooleanExpression containKeywordInTitle(String keyword) {
        return albumPost.title.contains(keyword);
    }

    private BooleanExpression containKeywordInContent(String keyword) {
        return albumPost.content.contains(keyword);
    }

    private BooleanExpression containKeywordInTitleOrContent(String keyword) {
        return albumPost.title.contains(keyword).or(albumPost.content.contains(keyword));
    }

    @Override
    public Optional<AlbumPost> findWithAlbumPostCommentsByAlbumPostId(Long albumPostId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(albumPost)
                .join(albumPost.author, user).fetchJoin()
                .leftJoin(albumPost.comments, albumPostComment).fetchJoin()
                .where(albumPost.albumPostId.eq(albumPostId))
                .fetchOne());
    }
}
