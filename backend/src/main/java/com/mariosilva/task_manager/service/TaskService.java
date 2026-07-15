package com.mariosilva.task_manager.service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.mariosilva.task_manager.document.Task;
import com.mariosilva.task_manager.repository.TaskRepository;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getTasksByUserId(String userId) {
        return taskRepository.findByUserId(userId);
    }

    public Optional<Task> getTaskById(String id) {
        return taskRepository.findById(id);
    }

    public Task createTask(Task task) {
        if (task.getCreatedAt() == null) {
            task.setCreatedAt(LocalDateTime.now());
        }
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    public Optional<Task> updateTask(String id, Task taskDetails) {
        return taskRepository.findById(id).map(existingTask -> {
            existingTask.setUserId(taskDetails.getUserId());
            existingTask.setTitle(taskDetails.getTitle());
            existingTask.setDescription(taskDetails.getDescription());
            existingTask.setStatus(taskDetails.getStatus());
            existingTask.setPriority(taskDetails.getPriority());
            existingTask.setTags(taskDetails.getTags());
            existingTask.setDueDate(taskDetails.getDueDate());
            existingTask.setUpdatedAt(LocalDateTime.now());
            return taskRepository.save(existingTask);
        });
    }

    public Optional<Task> patchTask(String id, Task taskDetails) {
        return taskRepository.findById(id).map(existingTask -> {
            if (taskDetails.getUserId() != null) {
                existingTask.setUserId(taskDetails.getUserId());
            }
            if (taskDetails.getTitle() != null) {
                existingTask.setTitle(taskDetails.getTitle());
            }
            if (taskDetails.getDescription() != null) {
                existingTask.setDescription(taskDetails.getDescription());
            }
            if (taskDetails.getStatus() != null) {
                existingTask.setStatus(taskDetails.getStatus());
            }
            if (taskDetails.getPriority() != null) {
                existingTask.setPriority(taskDetails.getPriority());
            }
            if (taskDetails.getTags() != null) {
                existingTask.setTags(taskDetails.getTags());
            }
            if (taskDetails.getDueDate() != null) {
                existingTask.setDueDate(taskDetails.getDueDate());
            }
            existingTask.setUpdatedAt(LocalDateTime.now());
            return taskRepository.save(existingTask);
        });
    }

    public boolean deleteTask(String id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
