package dgu.edu.dnaapi.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dgu.edu.dnaapi.domain.UserRole;
import dgu.edu.dnaapi.domain.dto.auth.QUnverifiedUser;
import dgu.edu.dnaapi.domain.dto.auth.UnverifiedUser;

import javax.persistence.EntityManager;
import java.util.List;

import static dgu.edu.dnaapi.domain.QUser.user;

public class UserRepositoryCustomImpl implements UserRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public UserRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public List<UnverifiedUser> findAllUnverifiedUser() {
        return queryFactory
                .select(new QUnverifiedUser(
                        user.email,
                        user.userName,
                        user.id,
                        user.studentId))
                .from(user)
                .where(user.role.eq(UserRole.UNVERIFIED_USER_ROLE))
                .fetch();
    }
}
