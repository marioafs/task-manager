package com.mariosilva.task_manager.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mariosilva.task_manager.document.Task;
import com.mariosilva.task_manager.enums.TaskPriority;
import com.mariosilva.task_manager.enums.TaskStatus;

public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByUserId(String userId);
    List<Task> findByUserIdAndStatus(String userId, TaskStatus status);
    List<Task> findByUserIdAndPriority(String userId, TaskPriority priority);
    List<Task> findByUserIdAndStatusAndPriority(String userId, TaskStatus status, TaskPriority priority);
}