package com.spring.repositoryS;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.spring.modelS.Team;

@Repository
public interface TeamRepository extends AbstractRepository<Team> {

	@Query("select p.team from Project p where p.id = ?1")
	Optional<Team> findTeamOfProject(Integer id);
	
}
