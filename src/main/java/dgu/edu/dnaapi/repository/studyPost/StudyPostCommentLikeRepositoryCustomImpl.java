package dgu.edu.dnaapi.repository.studyPost;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dgu.edu.dnaapi.domain.StudyPostCommentLike;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static dgu.edu.dnaapi.domain.QStudyPostComment.studyPostComment;
import static dgu.edu.dnaapi.domain.QStudyPostCommentLike.studyPostCommentLike;

public class StudyPostCommentLikeRepositoryCustomImpl implements StudyPostCommentLikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public StudyPostCommentLikeRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<StudyPostCommentLike> findWithStudyPostCommentByUserIdAndStudyPostCommentId(Long userId, Long studyPostCommentId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(studyPostCommentLike)
                .join(studyPostCommentLike.studyPostComment, studyPostComment).fetchJoin()
                .where(
                        studyPostCommentLike.user.id.eq(userId),
                        studyPostCommentLike.studyPostComment.studyPostCommentId.eq(studyPostCommentId))
                .fetchOne());
    }

    @Override
    public long deleteAllStudyPostCommentLikesByStudyPostCommentId(Long studyPostCommentId) {
        return queryFactory
                .delete(studyPostCommentLike)
                .where(studyPostCommentLike.studyPostComment.studyPostCommentId.eq(studyPostCommentId))
                .execute();
    }

    @Override
    public Set<Long> findStudyPostCommentIdsLikedByUserId(Long userId, List<Long> studyPostCommentIds) {
        return queryFactory
                .select(studyPostCommentLike.studyPostComment.studyPostCommentId)
                .from(studyPostCommentLike)
                .where(
                        studyPostCommentLike.user.id.eq(userId)
                                .and(studyPostCommentLike.studyPostComment.studyPostCommentId.in(studyPostCommentIds)))
                .fetch()
                .stream().collect(Collectors.toSet());
    }
}
