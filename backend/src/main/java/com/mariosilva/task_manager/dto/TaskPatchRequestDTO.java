package com.mariosilva.task_manager.dto;

import java.time.LocalDate;
import java.util.List;

import com.mariosilva.task_manager.enums.TaskPriority;
import com.mariosilva.task_manager.enums.TaskStatus;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskPatchRequestDTO {

    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    private List<String> tags;

    private LocalDate dueDate;
}
