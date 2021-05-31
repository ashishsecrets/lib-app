package com.ucsf.repository;

import com.ucsf.auth.model.User;
import com.ucsf.model.Notifications;
import com.ucsf.model.UserTasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTasksRepository extends JpaRepository<UserTasks, Long> {

    List<UserTasks> findByUserId(Long userId);

    List<UserTasks> findByUserIdAndTaskType(Long userId, String survey);

    UserTasks findByUserIdAndTaskId(Long userId, Long taskId);

    UserTasks findByTitle(String photographs);
}