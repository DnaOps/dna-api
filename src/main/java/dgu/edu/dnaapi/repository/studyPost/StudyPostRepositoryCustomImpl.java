package dgu.edu.dnaapi.repository.studyPost;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dgu.edu.dnaapi.controller.dto.PostSearchCondition;
import dgu.edu.dnaapi.domain.*;
import dgu.edu.dnaapi.domain.dto.studyPost.QStudyPostMetaDataResponseDto;
import dgu.edu.dnaapi.domain.dto.studyPost.StudyPostMetaDataResponseDto;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static dgu.edu.dnaapi.domain.QStudyPost.studyPost;
import static dgu.edu.dnaapi.domain.QStudyPostComment.*;
import static dgu.edu.dnaapi.domain.QUser.user;
import static org.springframework.util.StringUtils.hasText;

public class StudyPostRepositoryCustomImpl implements StudyPostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public StudyPostRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public List<StudyPostMetaDataResponseDto> search(PostSearchCondition condition, Pageable pageable) {
        return queryFactory
                .select(new QStudyPostMetaDataResponseDto(
                        studyPost.studyPostId,
                        studyPost.title,
                        studyPost.author.userName.as("author"),
                        studyPost.author.id.as("authorId"),
                        studyPost.commentCount,
                        studyPost.likeCount,
                        studyPost.author.level,
                        studyPost.lastModifiedDate.as("modifiedAt")))
                .from(studyPost)
                .join(studyPost.author, user)
                .where(
                        lowerThanLastStudyPostId(condition.getStart()),
                        containKeyword(condition.getTitle(), condition.getContent()),
                        authorNameEq(condition.getAuthor()))
                .orderBy(studyPost.studyPostId.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    private BooleanExpression lowerThanLastStudyPostId(Long lastStudyPostId) {
        return lastStudyPostId != null ? studyPost.studyPostId.lt(lastStudyPostId) : studyPost.studyPostId.lt(Long.MAX_VALUE);
    }

    private BooleanExpression authorNameEq(String author) {
        return hasText(author) ? studyPost.author.userName.eq(author) : null;
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
        return studyPost.title.contains(keyword);
    }

    private BooleanExpression containKeywordInContent(String keyword) {
        return studyPost.content.contains(keyword);
    }

    private BooleanExpression containKeywordInTitleOrContent(String keyword) {
        return studyPost.title.contains(keyword).or(studyPost.content.contains(keyword));
    }

    @Override
    public Optional<StudyPost> findWithStudyPostCommentsByStudyPostId(Long studyPostId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(studyPost)
                .join(studyPost.author, user).fetchJoin()
                .leftJoin(studyPost.comments, studyPostComment).fetchJoin()
                .where(studyPost.studyPostId.eq(studyPostId))
                .fetchOne());
    }

    @Override
    public Optional<StudyPost> findWithAuthorByStudyPostId(Long studyPostId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(studyPost)
                .join(studyPost.author, user).fetchJoin()
                .where(studyPost.studyPostId.eq(studyPostId))
                .fetchOne());
    }

    @Override
    public Long getStudyPostCountByUserId(Long userId) {
        return queryFactory
                .select(studyPost.count())
                .from(studyPost)
                .where(studyPost.author.id.eq(userId))
                .fetchOne();
    }
}
