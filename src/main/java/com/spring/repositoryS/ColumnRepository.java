package com.spring.repositoryS;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.spring.modelS.Column;
import com.spring.modelS.Project;
import com.spring.modelS.Task;
import com.spring.modelS.Workspace;

@Repository
public interface ColumnRepository extends AbstractRepository<Column> {

	@Query("select c from Column c where c.workspace.id = ?1")
	Collection<Column> findColumnsByWorkspace(int id);

	@Query("select t from Task t where t.column.id = ?1")
	Collection<Task> findAllTasksByColumn(int column);
	
	@Query("select c from Column c where c.workspace = ?1 and c.name = 'To do'")
	Column findColumnToDoByWorkspace(Workspace workspace);
	
	@Query("select c from Column c where c.workspace = ?1 and c.name = 'In progress'")
	Column findColumnInprogressByWorkspace(Workspace workspace);
	
	@Query("select c from Column c where c.workspace = ?1 and c.name = 'Done'")
	Column findColumnDoneByWorkspace(Workspace workspace);

	@Query("select c from Column c where c.workspace.sprint.project = ?1 and c.name = 'To do' order by c.workspace.sprint.startDate ASC")
	Collection<Column> findColumnToDoByProject(Project project);
}
