package com.mariosilva.task_manager.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.mariosilva.task_manager.enums.TaskPriority;
import com.mariosilva.task_manager.enums.TaskStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskResponseDTO {

    private String id;

    private String userId;

    private String title;

    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    private List<String> tags;

    private LocalDate dueDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
