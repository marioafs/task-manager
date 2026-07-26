package com.mariosilva.task_manager.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mariosilva.task_manager.document.User;
import com.mariosilva.task_manager.dto.RegisterRequestDTO;
import com.mariosilva.task_manager.dto.UserResponseDTO;
import com.mariosilva.task_manager.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        Optional<UserResponseDTO> existingEmail = userService.getUserByEmail(registerRequestDTO.getEmail());
        
        if (!existingEmail.isEmpty()) {
            return ResponseEntity.status(409).build();
        } 

        UserResponseDTO createdUser = userService.createUser(registerRequestDTO);
        return ResponseEntity.status(201).body(createdUser);
    
    }
    
    
}
