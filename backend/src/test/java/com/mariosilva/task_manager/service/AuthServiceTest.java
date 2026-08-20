package com.mariosilva.task_manager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.mariosilva.task_manager.dto.AuthResponseDTO;
import com.mariosilva.task_manager.dto.LoginRequestDTO;
import com.mariosilva.task_manager.security.service.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    // Happy Path Tests

    @Test
    @DisplayName("Should successfully authenticate user and return a valid JWT token")
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("mario@example.com");
        request.setPassword("password123");

        String expectedToken = "mocked.jwt.token.string";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        
        when(authentication.getName()).thenReturn("mario@example.com");
        
        when(jwtService.generateToken("mario@example.com")).thenReturn(expectedToken);

        AuthResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertEquals(expectedToken, response.getToken());
        
        // Verify that the dependencies were actually called during the process
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken("mario@example.com");
    }

    // Failure Scenarios

    @Test
    @DisplayName("Should throw BadCredentialsException when authentication fails")
    void login_ShouldThrowException_WhenCredentialsAreInvalid() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("mario@example.com");
        request.setPassword("wrongpassword");

        // simulate a failed Spring Security authentication
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Assert that the service propagates the exception and stops execution
        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
}