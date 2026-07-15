package com.mariosilva.task_manager.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mariosilva.task_manager.document.Task;

public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByUserId(String userId);
}