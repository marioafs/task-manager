package com.mariosilva.task_manager.service;

import org.springframework.stereotype.Service;

import com.mariosilva.task_manager.dto.AuthResponseDTO;
import com.mariosilva.task_manager.dto.LoginRequestDTO;


@Service
public class AuthService {


    public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {

        // TODO:
        // 1. Find user by email
        // 2. Compare password using BCrypt
        // 3. Generate JWT
        // 4. Return token

        return null;
    }

}