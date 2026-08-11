package com.mariosilva.task_manager.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mariosilva.task_manager.document.AppUser;
import com.mariosilva.task_manager.dto.AuthResponseDTO;
import com.mariosilva.task_manager.dto.LoginRequestDTO;
import com.mariosilva.task_manager.dto.RegisterRequestDTO;
import com.mariosilva.task_manager.dto.AppUserResponseDTO;
import com.mariosilva.task_manager.service.AuthService;
import com.mariosilva.task_manager.service.AppUserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private AppUserService userService;
    private final AuthService authService;

    public AuthController(AppUserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AppUserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        Optional<AppUserResponseDTO> existingEmail = userService.getUserByEmail(registerRequestDTO.getEmail());
        
        if (!existingEmail.isEmpty()) {
            return ResponseEntity.status(409).build();
        } 

        AppUserResponseDTO createdUser = userService.createUser(registerRequestDTO);
        return ResponseEntity.status(201).body(createdUser);
    
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    
}
