package com.mariosilva.task_manager.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mariosilva.task_manager.dto.AppUserResponseDTO;
import com.mariosilva.task_manager.dto.TaskPatchRequestDTO;
import com.mariosilva.task_manager.dto.TaskRequestDTO;
import com.mariosilva.task_manager.dto.TaskResponseDTO;
import com.mariosilva.task_manager.enums.TaskPriority;
import com.mariosilva.task_manager.enums.TaskStatus;
import com.mariosilva.task_manager.service.AppUserService;
import com.mariosilva.task_manager.service.TaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;
    private final AppUserService userService;

    public TaskController(TaskService taskService, AppUserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    // Users

    @GetMapping("/me")
    public ResponseEntity<List<TaskResponseDTO>> getMyTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            Authentication authentication) {
        String email = authentication.getName();

        return userService.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(taskService.getTasksByUserIdWithFilters(user.getId(), status, priority)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(
            @Valid @RequestBody TaskRequestDTO taskRequestDTO,
            Authentication authentication) {
        String email = authentication.getName();

        return userService.getUserByEmail(email)
                .map(user -> {
                    TaskResponseDTO createdTask = taskService.createTask(taskRequestDTO, user.getId());
                    return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(
            @PathVariable String id,
            Authentication authentication) {
        String email = authentication.getName();
        Optional<AppUserResponseDTO> currentUser = userService.getUserByEmail(email);

        if (currentUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        boolean isAdmin = isAdmin(authentication);

        return taskService.getTaskById(id)
                .map(task -> {
                    if (!task.getUserId().equals(currentUser.get().getId()) && !isAdmin) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<TaskResponseDTO>build();
                    }
                    return ResponseEntity.ok(task);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable String id,
            @Valid @RequestBody TaskRequestDTO taskDetails,
            Authentication authentication) {
        String email = authentication.getName();
        Optional<AppUserResponseDTO> currentUser = userService.getUserByEmail(email);

        if (currentUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        boolean isAdmin = isAdmin(authentication);
        Optional<TaskResponseDTO> existingTask = taskService.getTaskById(id);

        if (existingTask.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!existingTask.get().getUserId().equals(currentUser.get().getId()) && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return taskService.updateTask(id, taskDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> patchTask(
            @PathVariable String id,
            @RequestBody TaskPatchRequestDTO taskDetails,
            Authentication authentication) {
        String email = authentication.getName();
        Optional<AppUserResponseDTO> currentUser = userService.getUserByEmail(email);

        if (currentUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        boolean isAdmin = isAdmin(authentication);
        Optional<TaskResponseDTO> existingTask = taskService.getTaskById(id);

        if (existingTask.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!existingTask.get().getUserId().equals(currentUser.get().getId()) && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return taskService.patchTask(id, taskDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable String id,
            Authentication authentication) {
        String email = authentication.getName();
        Optional<AppUserResponseDTO> currentUser = userService.getUserByEmail(email);

        if (currentUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        boolean isAdmin = isAdmin(authentication);
        Optional<TaskResponseDTO> existingTask = taskService.getTaskById(id);

        if (existingTask.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!existingTask.get().getUserId().equals(currentUser.get().getId()) && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (taskService.deleteTask(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Admin

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getTasks(@RequestParam(required = false) String userId) {
        if (userId != null && !userId.isBlank()) {
            return ResponseEntity.ok(taskService.getTasksByUserId(userId));
        }
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskResponseDTO>> getTasksByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(taskService.getTasksByUserId(userId));
    }

    private boolean isAdmin(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }
}
