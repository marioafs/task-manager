package com.mariosilva.task_manager.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.json.JsonMapper;

import com.mariosilva.task_manager.config.IntegrationTestWithTestcontainer;
import com.mariosilva.task_manager.document.AppUser;
import com.mariosilva.task_manager.dto.LoginRequestDTO;
import com.mariosilva.task_manager.dto.RegisterRequestDTO;
import com.mariosilva.task_manager.enums.UserRole;
import com.mariosilva.task_manager.repository.AppUserRepository;


@AutoConfigureMockMvc
class AuthControllerIntegrationTest extends IntegrationTestWithTestcontainer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AppUserRepository userRepository;

    private AppUser mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new AppUser();
        mockUser.setId("user-id-123");
        mockUser.setEmail("mario@example.com");
        mockUser.setFirstName("Mario");
        mockUser.setLastName("Silva");
        mockUser.setPasswordHash(passwordEncoder.encode("password123"));
        mockUser.setRole(UserRole.USER);
    }

    // Register Endpoint Tests

    @Test
    @DisplayName("POST /api/auth/register - Should return 201 Created and the user DTO")
    void register_ShouldReturn201_WhenValidRequest() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setEmail("newuser@example.com");
        request.setFirstName("New");
        request.setLastName("User");
        request.setPassword("password123");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(AppUser.class))).thenReturn(mockUser);

        // Perform HTTP Request and assert responses
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("mario@example.com"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return 409 Conflict when email already exists")
    void register_ShouldReturn409_WhenEmailAlreadyExists() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setEmail("mario@example.com");
        request.setFirstName("Mario");
        request.setLastName("Silva");
        request.setPassword("password123");

        // Simulate that the email already exists in the database
        when(userRepository.findByEmail("mario@example.com")).thenReturn(Optional.of(mockUser));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    // Login Endpoint Tests

    @Test
    @DisplayName("POST /api/auth/login - Should return 200 OK and JWT token when credentials are valid")
    void login_ShouldReturn200AndToken_WhenCredentialsValid() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("mario@example.com");
        request.setPassword("password123");

        // Simulate finding the user in the database
        when(userRepository.findByEmail("mario@example.com")).thenReturn(Optional.of(mockUser));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    @DisplayName("POST /api/auth/login - Should return 403 Forbidden when credentials are invalid")
    void login_ShouldReturn403_WhenCredentialsInvalid() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("mario@example.com");
        request.setPassword("wrongpassword");

        // Simulate finding the user in the database (but passwords won't match)
        when(userRepository.findByEmail("mario@example.com")).thenReturn(Optional.of(mockUser));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}