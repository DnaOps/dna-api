package dgu.edu.dnaapi.repository.noticePost;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dgu.edu.dnaapi.controller.dto.PostSearchCondition;
import dgu.edu.dnaapi.domain.NoticePost;
import dgu.edu.dnaapi.domain.dto.noticePost.NoticePostMetaDataResponseDto;
import dgu.edu.dnaapi.domain.dto.noticePost.QNoticePostMetaDataResponseDto;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static dgu.edu.dnaapi.domain.QNoticePost.noticePost;
import static dgu.edu.dnaapi.domain.QNoticePostComment.noticePostComment;
import static dgu.edu.dnaapi.domain.QUser.user;
import static org.springframework.util.StringUtils.hasText;

public class NoticePostRepositoryCustomImpl implements NoticePostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public NoticePostRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<NoticePostMetaDataResponseDto> search(PostSearchCondition condition, Pageable pageable) {
        return queryFactory
                .select(new QNoticePostMetaDataResponseDto(
                        noticePost.noticePostId,
                        noticePost.title,
                        noticePost.author.userName.as("author"),
                        noticePost.author.id.as("authorId"),
                        noticePost.commentCount,
                        noticePost.likeCount,
                        noticePost.author.level,
                        noticePost.lastModifiedDate.as("modifiedAt"),
                        noticePost.isPinned))
                .from(noticePost)
                .join(noticePost.author, user)
                .where(
                        noticePost.isPinned.eq(false),
                        lowerThanLastForumId(condition.getStart()),
                        containKeyword(condition.getTitle(), condition.getContent()),
                        authorNameEq(condition.getAuthor()))
                .orderBy(noticePost.noticePostId.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    @Override
    public List<NoticePostMetaDataResponseDto> searchPinnedNoticePostMetaData() {
        return queryFactory
                .select(new QNoticePostMetaDataResponseDto(
                        noticePost.noticePostId,
                        noticePost.title,
                        noticePost.author.userName.as("author"),
                        noticePost.author.id.as("authorId"),
                        noticePost.commentCount,
                        noticePost.likeCount,
                        noticePost.author.level,
                        noticePost.lastModifiedDate.as("modifiedAt"),
                        noticePost.isPinned))
                .from(noticePost)
                .join(noticePost.author, user)
                .where(noticePost.isPinned.eq(true))
                .orderBy(noticePost.noticePostId.desc())
                .fetch();
    }

    private BooleanExpression lowerThanLastForumId(Long lastNoticePostId) {
        return lastNoticePostId != null ? noticePost.noticePostId.lt(lastNoticePostId) : noticePost.noticePostId.lt(Long.MAX_VALUE);
    }

    private BooleanExpression authorNameEq(String author) {
        return hasText(author) ? noticePost.author.userName.eq(author) : null;
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
        return noticePost.title.contains(keyword);
    }

    private BooleanExpression containKeywordInContent(String keyword) {
        return noticePost.content.contains(keyword);
    }

    private BooleanExpression containKeywordInTitleOrContent(String keyword) {
        return noticePost.title.contains(keyword).or(noticePost.content.contains(keyword));
    }

    @Override
    public Optional<NoticePost> findWithNoticePostCommentsByNoticePostId(Long noticePostId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(noticePost)
                .join(noticePost.author, user).fetchJoin()
                .leftJoin(noticePost.comments, noticePostComment).fetchJoin()
                .where(noticePost.noticePostId.eq(noticePostId))
                .fetchOne());
    }
}
