package dgu.edu.dnaapi.repository.forumPost;

import com.querydsl.core.types.dsl.BooleanExpression;
import dgu.edu.dnaapi.controller.dto.PostSearchCondition;
import dgu.edu.dnaapi.domain.ForumPost;
import dgu.edu.dnaapi.domain.dto.forumPost.ForumPostMetaDataResponseDto;
import dgu.edu.dnaapi.domain.dto.forumPost.QForumPostMetaDataResponseDto;
import org.springframework.data.domain.Pageable;
import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static dgu.edu.dnaapi.domain.QForumPost.forumPost;
import static dgu.edu.dnaapi.domain.QForumPostComment.forumPostComment;
import static dgu.edu.dnaapi.domain.QUser.*;
import static org.springframework.util.StringUtils.*;


public class ForumPostRepositoryCustomImpl implements ForumPostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ForumPostRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ForumPostMetaDataResponseDto> search(PostSearchCondition condition, Pageable pageable) {
        return queryFactory
                .select(new QForumPostMetaDataResponseDto(
                        forumPost.forumPostId,
                        forumPost.title,
                        forumPost.author.userName.as("author"),
                        forumPost.author.id.as("authorId"),
                        forumPost.commentCount,
                        forumPost.likeCount,
                        forumPost.author.level,
                        forumPost.lastModifiedDate.as("modifiedAt")))
                .from(forumPost)
                .leftJoin(forumPost.author, user)
                .where(
                        lowerThanLastForumPostId(condition.getStart()),
                        containKeyword(condition.getTitle(), condition.getContent()),
                        authorNameEq(condition.getAuthor()))
                .orderBy(forumPost.forumPostId.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    private BooleanExpression lowerThanLastForumPostId(Long lastForumPostId) {
        return lastForumPostId != null ? forumPost.forumPostId.lt(lastForumPostId) : forumPost.forumPostId.lt(Long.MAX_VALUE);
    }

    private BooleanExpression authorNameEq(String author) {
        return hasText(author) ? forumPost.author.userName.eq(author) : null;
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
        return forumPost.title.contains(keyword);
    }

    private BooleanExpression containKeywordInContent(String keyword) {
        return forumPost.content.contains(keyword);
    }

    private BooleanExpression containKeywordInTitleOrContent(String keyword) {
        return forumPost.title.contains(keyword).or(forumPost.content.contains(keyword));
    }

    @Override
    public Optional<ForumPost> findWithForumPostCommentsByForumPostId(Long forumPostId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(forumPost)
                .join(forumPost.author, user).fetchJoin()
                .leftJoin(forumPost.comments, forumPostComment).fetchJoin()
                .where(
                        forumPost.forumPostId.eq(forumPostId)
                )
                .fetchOne());

    }

    @Override
    public Long getForumPostCountByUserId(Long userId) {
        return queryFactory
                .select(forumPost.count())
                .from(forumPost)
                .where(forumPost.author.id.eq(userId))
                .fetchOne();
    }
}
