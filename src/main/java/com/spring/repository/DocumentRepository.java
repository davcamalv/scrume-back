package com.spring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.spring.model.Document;
import com.spring.model.DocumentType;
import com.spring.model.Sprint;

@Repository
public interface DocumentRepository extends AbstractRepository<Document> {
	List<Document> findBySprint(Sprint sprint);

	@Query(value = "select d.id from Document d where d.sprint = ?1 and d.name like %?2%")
	List<Integer> getDaily(Sprint sprint, String name);

	List<Document> findBySprintAndTypeAndNotified(Sprint sprint, DocumentType type, Boolean notified);
	
		

}
