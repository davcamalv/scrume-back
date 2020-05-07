package com.spring.repositoryS;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.spring.modelS.Notification;
import com.spring.modelS.Sprint;
import com.spring.modelS.Team;
import com.spring.modelS.User;

@Repository
public interface NotificationRepository extends AbstractRepository<Notification> {

	@Query("select n from Notification n where n.sprint.project.team = ?2 and (n.user = null or n.user = ?1) and n.date < current_timestamp")
	Collection<Notification> listByUser(User user, Team team);
	
	@Query("select n from Notification n where n.sprint = ?1 and n.date > current_timestamp and n.user = null")
	Collection<Notification> listAllInactiveNotifications(Sprint sprint);

}
