package com.mariosilva.task_manager.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import tools.jackson.databind.json.JsonMapper;

import com.mariosilva.task_manager.config.IntegrationTestWithTestcontainer;
import com.mariosilva.task_manager.config.TestcontainersConfiguration;
import com.mariosilva.task_manager.document.AppUser;
import com.mariosilva.task_manager.dto.LoginRequestDTO;
import com.mariosilva.task_manager.dto.RegisterRequestDTO;
import com.mariosilva.task_manager.enums.UserRole;
import com.mariosilva.task_manager.repository.AppUserRepository;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.junit.jupiter.api.BeforeEach;

@AutoConfigureMockMvc
class AuthIntegrationTest extends IntegrationTestWithTestcontainer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    // Registration Flow

    @Test
    @DisplayName("Should register a new user and save it to the real MongoDB")
    void registerUser_Success() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setEmail("realdb@example.com");
        request.setFirstName("Real");
        request.setLastName("Database");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("realdb@example.com"));

        // Verify the user actually exists in the real database
        assertTrue(userRepository.findByEmail("realdb@example.com").isPresent());
    }

    @Test
    @DisplayName("Should prevent registration with an existing email in the database")
    void registerUser_DuplicateEmail() throws Exception {
        // Add user to the database
        AppUser existingUser = new AppUser();
        existingUser.setEmail("duplicate@example.com");
        existingUser.setPasswordHash(passwordEncoder.encode("password123"));
        existingUser.setFirstName("First");
        existingUser.setLastName("Last");
        existingUser.setRole(UserRole.USER);
        userRepository.save(existingUser);

        // Attempt to register with the same email
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setEmail("duplicate@example.com");
        request.setFirstName("Another");
        request.setLastName("User");
        request.setPassword("newpassword123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
        
        // Ensure only 1 user exists in the DB (the original one)
        assertEquals(1, userRepository.count());
    }

    // Login Flow

    @Test
    @DisplayName("Should login successfully and return JWT for an existing user")
    void loginUser_Success() throws Exception {
        // Pre-populate the real database
        AppUser user = new AppUser();
        user.setEmail("login@example.com");
        user.setPasswordHash(passwordEncoder.encode("securepassword"));
        user.setFirstName("Login");
        user.setLastName("User");
        user.setRole(UserRole.USER);
        userRepository.save(user);

        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("login@example.com");
        loginRequest.setPassword("securepassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").isNotEmpty());;
    }

    @Test
    @DisplayName("Should reject login with wrong password against the real database")
    void loginUser_WrongPassword() throws Exception {
        AppUser user = new AppUser();
        user.setEmail("login@example.com");
        user.setPasswordHash(passwordEncoder.encode("securepassword"));
        user.setFirstName("Login");
        user.setLastName("User");
        user.setRole(UserRole.USER);
        userRepository.save(user);

        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("login@example.com");
        loginRequest.setPassword("wrongpassword"); // Invalid password

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden());
    }
}