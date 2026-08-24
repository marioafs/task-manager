package com.mariosilva.task_manager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.mariosilva.task_manager.document.Task;
import com.mariosilva.task_manager.dto.TaskPatchRequestDTO;
import com.mariosilva.task_manager.dto.TaskRequestDTO;
import com.mariosilva.task_manager.dto.TaskResponseDTO;
import com.mariosilva.task_manager.enums.TaskPriority;
import com.mariosilva.task_manager.enums.TaskStatus;
import com.mariosilva.task_manager.repository.TaskRepository;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskResponseDTO> getAllTasks() {
        List<TaskResponseDTO> tasks = new ArrayList<>();
        for (Task task : taskRepository.findAll()) {
            tasks.add(convertToTaskResponseDTO(task));
        }
        return tasks;
    }

    public List<TaskResponseDTO> getTasksByUserId(String userId) {
        List<TaskResponseDTO> tasks = new ArrayList<>();
        for (Task task : taskRepository.findByUserId(userId)) {
            tasks.add(convertToTaskResponseDTO(task));
        }
        return tasks;
    }

    public List<TaskResponseDTO> getTasksByUserIdWithFilters(String userId, TaskStatus status, TaskPriority priority) {
        List<Task> tasks;
        if (status != null && priority != null) {
            tasks = taskRepository.findByUserIdAndStatusAndPriority(userId, status, priority);
        } else if (status != null) {
            tasks = taskRepository.findByUserIdAndStatus(userId, status);
        } else if (priority != null) {
            tasks = taskRepository.findByUserIdAndPriority(userId, priority);
        } else {
            tasks = taskRepository.findByUserId(userId);
        }

        List<TaskResponseDTO> dtos = new ArrayList<>();
        for (Task task : tasks) {
            dtos.add(convertToTaskResponseDTO(task));
        }
        return dtos;
    }

    public Optional<TaskResponseDTO> getTaskById(String id) {
        return taskRepository.findById(id).map(this::convertToTaskResponseDTO);
    }

    public TaskResponseDTO createTask(TaskRequestDTO requestDTO, String userId) {
        Task task = taskRequestDTOToTask(requestDTO, userId);
        LocalDateTime now = LocalDateTime.now();
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        Task savedTask = taskRepository.save(task);
        return convertToTaskResponseDTO(savedTask);
    }

    public Optional<TaskResponseDTO> updateTask(String id, TaskRequestDTO taskDetails) {
        return taskRepository.findById(id).map(existingTask -> {
            existingTask.setTitle(taskDetails.getTitle());
            existingTask.setDescription(taskDetails.getDescription());
            if (taskDetails.getStatus() != null) {
                existingTask.setStatus(taskDetails.getStatus());
            }
            if (taskDetails.getPriority() != null) {
                existingTask.setPriority(taskDetails.getPriority());
            }
            existingTask.setTags(taskDetails.getTags());
            existingTask.setDueDate(taskDetails.getDueDate());
            existingTask.setUpdatedAt(LocalDateTime.now());
            Task updatedTask = taskRepository.save(existingTask);
            return convertToTaskResponseDTO(updatedTask);
        });
    }

    public Optional<TaskResponseDTO> patchTask(String id, TaskPatchRequestDTO taskDetails) {
        return taskRepository.findById(id).map(existingTask -> {
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
            Task updatedTask = taskRepository.save(existingTask);
            return convertToTaskResponseDTO(updatedTask);
        });
    }

    public boolean deleteTask(String id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private TaskResponseDTO convertToTaskResponseDTO(Task task) {
        if (task == null) {
            return null;
        }
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setUserId(task.getUserId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setTags(task.getTags());
        dto.setDueDate(task.getDueDate());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        return dto;
    }

    private Task taskRequestDTOToTask(TaskRequestDTO dto, String userId) {
        Task task = new Task();
        task.setUserId(userId);
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus() != null ? dto.getStatus() : TaskStatus.TODO);
        task.setPriority(dto.getPriority() != null ? dto.getPriority() : TaskPriority.MEDIUM);
        task.setTags(dto.getTags());
        task.setDueDate(dto.getDueDate());
        return task;
    }
}
