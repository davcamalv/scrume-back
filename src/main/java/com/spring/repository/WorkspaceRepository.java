package com.spring.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.spring.model.HistoryTask;
import com.spring.model.Sprint;
import com.spring.model.Team;
import com.spring.model.Workspace;

@Repository
public interface WorkspaceRepository extends AbstractRepository<Workspace> {

	@Query("select w from Workspace w where w.sprint.project.team.id = ?1")
	Collection<Workspace> findWorkspacesByTeam(int id);

	@Query("select uRol.team from UserRol uRol where uRol.user.userAccount.id = ?1 and uRol.admin = true")
	Collection<Team> findAllTeamsByUserAccountAdmin(int userAccount);

	@Query("select w from Workspace w where w.sprint.id = ?1")
	Collection<Workspace> findWorkspacesBySprint(int sprint);
	
	@Query("select ht from HistoryTask ht where ht.task.project.id = ?1 order by ht.date DESC")
	Collection<HistoryTask> findAllHistoryTasksByProject(int project);

	@Query("select w from Workspace w where w.sprint = ?1 order by w.id asc")
	List<Workspace> getFirstProjectOfASprint(Sprint sprint);
}
