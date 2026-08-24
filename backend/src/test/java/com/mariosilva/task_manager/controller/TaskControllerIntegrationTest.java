package com.mariosilva.task_manager.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.mariosilva.task_manager.config.IntegrationTestWithTestcontainer;
import com.mariosilva.task_manager.document.AppUser;
import com.mariosilva.task_manager.dto.AppUserResponseDTO;
import com.mariosilva.task_manager.dto.TaskPatchRequestDTO;
import com.mariosilva.task_manager.dto.TaskRequestDTO;
import com.mariosilva.task_manager.dto.TaskResponseDTO;
import com.mariosilva.task_manager.enums.TaskPriority;
import com.mariosilva.task_manager.enums.TaskStatus;
import com.mariosilva.task_manager.enums.UserRole;
import com.mariosilva.task_manager.repository.AppUserRepository;
import com.mariosilva.task_manager.security.service.JwtService;
import com.mariosilva.task_manager.service.AppUserService;
import com.mariosilva.task_manager.service.TaskService;

import tools.jackson.databind.json.JsonMapper;

@AutoConfigureMockMvc
class TaskControllerIntegrationTest extends IntegrationTestWithTestcontainer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private AppUserService userService;

    @MockitoBean
    private AppUserRepository userRepository;

    private String userToken;
    private String adminToken;

    private AppUser mockUser;
    private AppUser mockAdmin;
    private AppUserResponseDTO regularUserDTO;
    private AppUserResponseDTO adminUserDTO;
    private TaskResponseDTO userTask;
    private TaskResponseDTO otherUserTask;

    @BeforeEach
    void setUp() {
        mockUser = new AppUser();
        mockUser.setId("user-1");
        mockUser.setEmail("user@example.com");
        mockUser.setFirstName("Regular");
        mockUser.setLastName("User");
        mockUser.setPasswordHash(passwordEncoder.encode("password123"));
        mockUser.setRole(UserRole.USER);

        mockAdmin = new AppUser();
        mockAdmin.setId("admin-1");
        mockAdmin.setEmail("admin@example.com");
        mockAdmin.setFirstName("Admin");
        mockAdmin.setLastName("User");
        mockAdmin.setPasswordHash(passwordEncoder.encode("adminpass"));
        mockAdmin.setRole(UserRole.ADMIN);

        regularUserDTO = new AppUserResponseDTO();
        regularUserDTO.setId("user-1");
        regularUserDTO.setEmail("user@example.com");
        regularUserDTO.setFirstName("Regular");
        regularUserDTO.setLastName("User");

        adminUserDTO = new AppUserResponseDTO();
        adminUserDTO.setId("admin-1");
        adminUserDTO.setEmail("admin@example.com");
        adminUserDTO.setFirstName("Admin");
        adminUserDTO.setLastName("User");

        userTask = new TaskResponseDTO();
        userTask.setId("task-1");
        userTask.setUserId("user-1");
        userTask.setTitle("My Task");
        userTask.setDescription("Description 1");
        userTask.setStatus(TaskStatus.TODO);
        userTask.setPriority(TaskPriority.HIGH);
        userTask.setDueDate(LocalDate.now().plusDays(3));
        userTask.setCreatedAt(LocalDateTime.now());
        userTask.setUpdatedAt(LocalDateTime.now());

        otherUserTask = new TaskResponseDTO();
        otherUserTask.setId("task-2");
        otherUserTask.setUserId("user-2");
        otherUserTask.setTitle("Other User Task");
        otherUserTask.setDescription("Description 2");
        otherUserTask.setStatus(TaskStatus.IN_PROGRESS);
        otherUserTask.setPriority(TaskPriority.LOW);
        otherUserTask.setCreatedAt(LocalDateTime.now());
        otherUserTask.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(mockAdmin));

        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(regularUserDTO));
        when(userService.getUserByEmail("admin@example.com")).thenReturn(Optional.of(adminUserDTO));

        userToken = "Bearer " + jwtService.generateToken("user@example.com");
        adminToken = "Bearer " + jwtService.generateToken("admin@example.com");
    }

    // --- User Endpoints (/me, CRUD on own tasks) ---

    @Test
    @DisplayName("GET /api/tasks/me - Should return current user tasks")
    void getMyTasks_Success() throws Exception {
        when(taskService.getTasksByUserIdWithFilters("user-1", null, null))
                .thenReturn(List.of(userTask));

        mockMvc.perform(get("/api/tasks/me")
                .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("task-1"))
                .andExpect(jsonPath("$[0].title").value("My Task"))
                .andExpect(jsonPath("$[0].userId").value("user-1"));
    }

    @Test
    @DisplayName("POST /api/tasks - Should create task for current user and return 201")
    void createTask_Success() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle("New Task");
        request.setDescription("New Description");
        request.setPriority(TaskPriority.MEDIUM);

        when(taskService.createTask(any(TaskRequestDTO.class), eq("user-1")))
                .thenReturn(userTask);

        mockMvc.perform(post("/api/tasks")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("task-1"))
                .andExpect(jsonPath("$.title").value("My Task"));
    }

    @Test
    @DisplayName("POST /api/tasks - Should return 400 when title is blank")
    void createTask_ValidationError_BlankTitle() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle(""); // Invalid blank title

        mockMvc.perform(post("/api/tasks")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/tasks/{id} - Should return task if owned by user")
    void getTaskById_OwnTask_Success() throws Exception {
        when(taskService.getTaskById("task-1")).thenReturn(Optional.of(userTask));

        mockMvc.perform(get("/api/tasks/task-1")
                .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("task-1"))
                .andExpect(jsonPath("$.userId").value("user-1"));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} - Should return 403 Forbidden if task belongs to another user")
    void getTaskById_OtherUserTask_Forbidden() throws Exception {
        when(taskService.getTaskById("task-2")).thenReturn(Optional.of(otherUserTask));

        mockMvc.perform(get("/api/tasks/task-2")
                .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/tasks/{id} - Should return 404 when task not found")
    void getTaskById_NotFound() throws Exception {
        when(taskService.getTaskById("non-existent")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tasks/non-existent")
                .header("Authorization", userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} - Should update task when owned by user")
    void updateTask_OwnTask_Success() throws Exception {
        TaskRequestDTO updateRequest = new TaskRequestDTO();
        updateRequest.setTitle("Updated Title");
        updateRequest.setStatus(TaskStatus.DONE);

        when(taskService.getTaskById("task-1")).thenReturn(Optional.of(userTask));
        when(taskService.updateTask(eq("task-1"), any(TaskRequestDTO.class))).thenReturn(Optional.of(userTask));

        mockMvc.perform(put("/api/tasks/task-1")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} - Should return 403 when updating another user's task")
    void updateTask_OtherUserTask_Forbidden() throws Exception {
        TaskRequestDTO updateRequest = new TaskRequestDTO();
        updateRequest.setTitle("Updated Title");

        when(taskService.getTaskById("task-2")).thenReturn(Optional.of(otherUserTask));

        mockMvc.perform(put("/api/tasks/task-2")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH /api/tasks/{id} - Should patch task when owned by user")
    void patchTask_OwnTask_Success() throws Exception {
        TaskPatchRequestDTO patchRequest = new TaskPatchRequestDTO();
        patchRequest.setStatus(TaskStatus.DONE);

        when(taskService.getTaskById("task-1")).thenReturn(Optional.of(userTask));
        when(taskService.patchTask(eq("task-1"), any(TaskPatchRequestDTO.class))).thenReturn(Optional.of(userTask));

        mockMvc.perform(patch("/api/tasks/task-1")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/tasks/{id} - Should return 403 when patching another user's task")
    void patchTask_OtherUserTask_Forbidden() throws Exception {
        TaskPatchRequestDTO patchRequest = new TaskPatchRequestDTO();
        patchRequest.setStatus(TaskStatus.DONE);

        when(taskService.getTaskById("task-2")).thenReturn(Optional.of(otherUserTask));

        mockMvc.perform(patch("/api/tasks/task-2")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} - Should delete task and return 204 when owned by user")
    void deleteTask_OwnTask_Success() throws Exception {
        when(taskService.getTaskById("task-1")).thenReturn(Optional.of(userTask));
        when(taskService.deleteTask("task-1")).thenReturn(true);

        mockMvc.perform(delete("/api/tasks/task-1")
                .header("Authorization", userToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} - Should return 403 when deleting another user's task")
    void deleteTask_OtherUserTask_Forbidden() throws Exception {
        when(taskService.getTaskById("task-2")).thenReturn(Optional.of(otherUserTask));

        mockMvc.perform(delete("/api/tasks/task-2")
                .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }

    // --- Admin Endpoints (/api/tasks, /api/tasks/user/{userId}, and admin overrides) ---

    @Test
    @DisplayName("GET /api/tasks - Admin should be able to get all tasks")
    void getTasks_AsAdmin_Success() throws Exception {
        when(taskService.getAllTasks()).thenReturn(List.of(userTask, otherUserTask));

        mockMvc.perform(get("/api/tasks")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/tasks - Regular user should be forbidden (403) by security config")
    void getTasks_AsRegularUser_Forbidden() throws Exception {
        mockMvc.perform(get("/api/tasks")
                .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/tasks/user/{userId} - Admin should get tasks by user ID")
    void getTasksByUserId_AsAdmin_Success() throws Exception {
        when(taskService.getTasksByUserId("user-2")).thenReturn(List.of(otherUserTask));

        mockMvc.perform(get("/api/tasks/user/user-2")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("user-2"));
    }

    @Test
    @DisplayName("GET /api/tasks/user/{userId} - Regular user should be forbidden (403)")
    void getTasksByUserId_AsRegularUser_Forbidden() throws Exception {
        mockMvc.perform(get("/api/tasks/user/user-2")
                .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/tasks/{id} - Admin can access any task")
    void getTaskById_AsAdmin_Success() throws Exception {
        when(taskService.getTaskById("task-2")).thenReturn(Optional.of(otherUserTask));

        mockMvc.perform(get("/api/tasks/task-2")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("task-2"));
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} - Admin can delete any user's task")
    void deleteTask_AsAdmin_Success() throws Exception {
        when(taskService.getTaskById("task-2")).thenReturn(Optional.of(otherUserTask));
        when(taskService.deleteTask("task-2")).thenReturn(true);

        mockMvc.perform(delete("/api/tasks/task-2")
                .header("Authorization", adminToken))
                .andExpect(status().isNoContent());
    }
}
