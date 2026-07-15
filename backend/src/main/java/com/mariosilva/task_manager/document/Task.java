package com.mariosilva.task_manager.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import com.mariosilva.task_manager.enums.TaskPriority;
import com.mariosilva.task_manager.enums.TaskStatus;


@Getter
@Setter
@NoArgsConstructor
@Document(collection = "tasks")
public class Task {
    @Id
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
