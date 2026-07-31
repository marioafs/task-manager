package com.mariosilva.task_manager.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.mariosilva.task_manager.dto.AuthResponseDTO;
import com.mariosilva.task_manager.dto.LoginRequestDTO;
import com.mariosilva.task_manager.security.service.JwtService;


@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponseDTO login(LoginRequestDTO request) {

        UsernamePasswordAuthenticationToken userPass = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(userPass);
        String token = jwtService.generateToken(authentication.getName());
        return new AuthResponseDTO(token);
        
    }

}