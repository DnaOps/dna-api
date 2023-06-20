package dgu.edu.dnaapi.repository.studyPost;

import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import static dgu.edu.dnaapi.domain.QStudyPostLike.studyPostLike;

public class StudyPostLikeRepositoryCustomImpl implements StudyPostLikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public StudyPostLikeRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public boolean findStudyPostLikeByStudyPostIdAndUserId(Long studyPostId, Long userId) {
        return queryFactory
                .selectOne()
                .from(studyPostLike)
                .where(
                        studyPostLike.studyPost.studyPostId.eq(studyPostId),
                        studyPostLike.user.id.eq(userId)
                ).fetchFirst() != null;
    }
}
