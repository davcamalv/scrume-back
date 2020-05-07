package com.spring.jwtS;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.spring.modelS.User;
import com.spring.modelS.UserAccount;
import com.spring.repositoryS.AbstractRepository;

@Repository
public interface JwtUserAccountRepository extends AbstractRepository<UserAccount> {
	
	Boolean existsByUsername(String username);

	@Query("select u from UserAccount u where u.username = ?1")
	Optional<UserAccount> findByEmail(String username);
	
	@Query("select u from User u join u.userAccount ua where ua.username = ?1")
	Optional<User> findUserByUserName(String username);
}
