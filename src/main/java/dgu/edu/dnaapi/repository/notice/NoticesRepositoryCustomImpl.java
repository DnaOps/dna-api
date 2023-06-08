package dgu.edu.dnaapi.repository.notice;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dgu.edu.dnaapi.controller.dto.BoardSearchCondition;
import dgu.edu.dnaapi.domain.Notices;
import dgu.edu.dnaapi.domain.dto.notices.NoticesMetaDataResponseDto;
import dgu.edu.dnaapi.domain.dto.notices.QNoticesMetaDataResponseDto;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static dgu.edu.dnaapi.domain.QNotices.notices;
import static dgu.edu.dnaapi.domain.QUser.user;
import static org.springframework.util.StringUtils.hasText;

public class NoticesRepositoryCustomImpl implements NoticesRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public NoticesRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<NoticesMetaDataResponseDto> search(BoardSearchCondition condition, Pageable pageable) {
        return queryFactory
                .select(new QNoticesMetaDataResponseDto(
                        notices.noticeId,
                        notices.title,
                        notices.author.userName.as("author"),
                        notices.author.id.as("authorId"),
                        notices.commentCount,
                        notices.likeCount,
                        notices.author.level,
                        notices.lastModifiedDate.as("modifiedAt")))
                .from(notices)
                .join(notices.author, user).fetchJoin()
                .where(
                        lowerThanLastForumId(condition.getStart()),
                        containKeyword(condition.getTitle(), condition.getContent()),
                        authorNameEq(condition.getAuthor()))
                .orderBy(notices.noticeId.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    private BooleanExpression lowerThanLastForumId(Long lastNoticeId) {
        return lastNoticeId != null ? notices.noticeId.lt(lastNoticeId) : notices.noticeId.lt(Long.MAX_VALUE);
    }

    private BooleanExpression authorNameEq(String author) {
        return hasText(author) ? notices.author.userName.eq(author) : null;
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
        return notices.title.contains(keyword);
    }

    private BooleanExpression containKeywordInContent(String keyword) {
        return notices.content.contains(keyword);
    }

    private BooleanExpression containKeywordInTitleOrContent(String keyword) {
        return notices.title.contains(keyword).or(notices.content.contains(keyword));
    }

    @Override
    public Optional<Notices> findByIdWithComments(Long noticeId) {
        return Optional.empty();
    }
}
