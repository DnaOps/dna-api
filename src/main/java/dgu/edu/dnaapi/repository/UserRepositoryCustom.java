package dgu.edu.dnaapi.repository;

import dgu.edu.dnaapi.domain.dto.auth.UnverifiedUser;

import java.util.List;

public interface UserRepositoryCustom {

    List<UnverifiedUser> findAllUnverifiedUser();
}
