package com.spring.repositoryS;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import com.spring.modelS.BaseEntity;
import com.spring.modelS.UserAccount;

@NoRepositoryBean
public interface AbstractRepository<T extends BaseEntity> extends JpaRepository<T, Integer> {

	@Query("select ua from UserAccount ua where ua.username = ?1")
	Optional<UserAccount> findByUserName(String username);
}
