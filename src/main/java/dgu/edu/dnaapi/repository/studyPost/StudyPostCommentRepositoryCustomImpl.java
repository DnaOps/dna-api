package dgu.edu.dnaapi.repository.studyPost;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dgu.edu.dnaapi.domain.StudyPostComment;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static dgu.edu.dnaapi.domain.QStudyPost.studyPost;
import static dgu.edu.dnaapi.domain.QStudyPostComment.studyPostComment;
import static dgu.edu.dnaapi.domain.QUser.user;

public class StudyPostCommentRepositoryCustomImpl implements StudyPostCommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public StudyPostCommentRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<StudyPostComment> search(Long studyPostId, Long start, Pageable pageable) {
        return queryFactory
                .selectFrom(studyPostComment)
                .join(studyPostComment.studyPost, studyPost).fetchJoin()
                .join(studyPostComment.author, user).fetchJoin()
                .where(
                        studyPostComment.commentGroupId.isNull(),
                        studyPostComment.studyPost.studyPostId.eq(studyPostId),
                        greaterThanLastStudyPostCommentId(start))
                .orderBy(studyPostComment.studyPostCommentId.asc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }
    private BooleanExpression greaterThanLastStudyPostCommentId(Long lastStudyPostCommentId) {
        return lastStudyPostCommentId != null ? studyPostComment.studyPostCommentId.gt(lastStudyPostCommentId) : studyPostComment.studyPostCommentId.gt(0);
    }

    @Override
    public List<StudyPostComment> findAllReplyStudyPostComments(Long studyPostId, List<Long> commentIds) {
        return queryFactory
                .selectFrom(studyPostComment)
                .leftJoin(studyPostComment.parent).fetchJoin()
                .join(studyPostComment.author, user).fetchJoin()
                .join(studyPostComment.studyPost, studyPost).fetchJoin()
                .where(
                        studyPostComment.studyPost.studyPostId.eq(studyPostId),
                        studyPostComment.commentGroupId.in(commentIds))
                .orderBy(
                        studyPostComment.parent.studyPostCommentId.asc(),
                        studyPostComment.createdDate.asc())
                .fetch();
    }

    @Override
    public List<StudyPostComment> findAllReplyStudyPostCommentsByCommentGroupId(Long studyPostId, Long commentGroupId) {
        return queryFactory
                .selectFrom(studyPostComment)
                .leftJoin(studyPostComment.parent).fetchJoin()
                .join(studyPostComment.studyPost, studyPost).fetchJoin()
                .join(studyPostComment.author, user).fetchJoin()
                .where(isInStudyPostCommentsGroup(studyPostId, commentGroupId))
                .orderBy(
                        studyPostComment.parent.studyPostCommentId.asc(),
                        studyPostComment.createdDate.asc())
                .fetch();
    }

    private BooleanExpression isInStudyPostCommentsGroup(Long studyPostId, Long commentGroupId) {
        return studyPostComment.studyPostCommentId.eq(commentGroupId)
                .or(studyPostComment.studyPost.studyPostId.eq(studyPostId).and(studyPostComment.commentGroupId.eq(commentGroupId)));
    }

    @Override
    public List<StudyPostComment> findAllStudyPostCommentsWithStudyPostByStudyPostId(Long studyPostId) {
        return queryFactory
                .selectFrom(studyPostComment)
                .leftJoin(studyPostComment.parent).fetchJoin()
                .join(studyPostComment.studyPost, studyPost).fetchJoin()
                .where(studyPostComment.studyPost.studyPostId.eq(studyPostId))
                .orderBy(
                        studyPostComment.parent.studyPostCommentId.asc().nullsFirst(),
                        studyPostComment.createdDate.asc())
                .fetch();
    }
}
