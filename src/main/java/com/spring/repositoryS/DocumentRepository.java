package com.spring.repositoryS;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.spring.modelS.Document;
import com.spring.modelS.DocumentType;
import com.spring.modelS.Sprint;

@Repository
public interface DocumentRepository extends AbstractRepository<Document> {
	List<Document> findBySprint(Sprint sprint);

	@Query(value = "select d.id from Document d where d.sprint = ?1 and d.name like %?2%")
	List<Integer> getDaily(Sprint sprint, String name);

	List<Document> findBySprintAndTypeAndNotified(Sprint sprint, DocumentType type, Boolean notified);
	
		

}
