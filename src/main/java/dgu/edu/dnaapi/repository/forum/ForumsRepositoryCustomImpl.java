package dgu.edu.dnaapi.repository.forum;

import com.querydsl.core.types.dsl.BooleanExpression;
import dgu.edu.dnaapi.controller.dto.BoardSearchCondition;
import dgu.edu.dnaapi.domain.Forums;
import dgu.edu.dnaapi.domain.dto.forum.ForumsMetaDataResponseDto;
import dgu.edu.dnaapi.domain.dto.forum.QForumsMetaDataResponseDto;
import org.springframework.data.domain.Pageable;
import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static dgu.edu.dnaapi.domain.QForumComments.*;
import static dgu.edu.dnaapi.domain.QForums.forums;
import static dgu.edu.dnaapi.domain.QUser.*;
import static org.springframework.util.StringUtils.*;


public class ForumsRepositoryCustomImpl implements ForumsRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public ForumsRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ForumsMetaDataResponseDto> search(BoardSearchCondition condition, Pageable pageable) {
        return queryFactory
                .select(new QForumsMetaDataResponseDto(
                        forums.forumId,
                        forums.title,
                        forums.author.userName.as("author"),
                        forums.author.id.as("authorId"),
                        forums.commentCount,
                        forums.likeCount,
                        forums.author.level,
                        forums.lastModifiedDate.as("modifiedAt")))
                .from(forums)
                .leftJoin(forums.author, user)
                .where(
                        lowerThanLastForumId(condition.getStart()),
                        containKeyword(condition.getTitle(), condition.getContent()),
                        authorNameEq(condition.getAuthor()))
                .orderBy(forums.forumId.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    private BooleanExpression lowerThanLastForumId(Long lastForumId) {
        return lastForumId != null ? forums.forumId.lt(lastForumId) : forums.forumId.lt(Long.MAX_VALUE);
    }

    private BooleanExpression authorNameEq(String author) {
        return hasText(author) ? forums.author.userName.eq(author) : null;
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
        return forums.title.contains(keyword);
    }

    private BooleanExpression containKeywordInContent(String keyword) {
        return forums.content.contains(keyword);
    }

    private BooleanExpression containKeywordInTitleOrContent(String keyword) {
        return forums.title.contains(keyword).or(forums.content.contains(keyword));
    }

    @Override
    public Optional<Forums> findByIdWithComments(Long forumId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(forums)
                .join(forums.author, user).fetchJoin()
                .leftJoin(forums.comments, forumComments).fetchJoin()
                .where(
                        forums.forumId.eq(forumId)
                )
                .fetchOne());

    }
}
