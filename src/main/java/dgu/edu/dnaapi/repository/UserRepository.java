package dgu.edu.dnaapi.repository;

import dgu.edu.dnaapi.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    public User findByUserName(String userName);
    public User findByOauthId(String oauthId);

    public Optional<User> findUserByEmail(String email);

    public User findByEmail(String email);
}
