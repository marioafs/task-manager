package com.mariosilva.task_manager.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    private JwtService jwtService;

    private final String testSecretKey = "abcde-efghi-jklmn-opqrs-tuvwx-yz0";
    private final long testExpirationMs = 86400000; // 24h
    private final String testEmail = "mario@example.com";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(testSecretKey, testExpirationMs);
    }

    // Happy Path Tests

    @Test
    @DisplayName("Should generate a non-null and non-empty token")
    void generateToken_ShouldReturnValidToken() {
        String token = jwtService.generateToken(testEmail);

        assertNotNull(token);
        assertFalse(token.trim().isEmpty());
    }

    @Test
    @DisplayName("Should validate token and extract the correct email")
    void validateAndExtractEmail_ShouldReturnEmail_WhenTokenIsValid() {
        String token = jwtService.generateToken(testEmail);

        Optional<String> extractedEmail = jwtService.validateAndExtractEmail(token);

        assertTrue(extractedEmail.isPresent());
        assertEquals(testEmail, extractedEmail.get());
    }

    // Failure Scenarios

    @Test
    @DisplayName("Should return Optional.empty when token is malformed")
    void validateAndExtractEmail_ShouldReturnEmpty_WhenTokenIsMalformed() {
        String malformedToken = "invalid.jwt.token.structure";

        Optional<String> result = jwtService.validateAndExtractEmail(malformedToken);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return Optional.empty when token signature is untrusted")
    void validateAndExtractEmail_ShouldReturnEmpty_WhenSignatureIsInvalid() {
        JwtService untrustedJwtService = new JwtService("untrusted-secret-key-1234567890", testExpirationMs);
        String forgedToken = untrustedJwtService.generateToken(testEmail);

        Optional<String> result = jwtService.validateAndExtractEmail(forgedToken);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return Optional.empty when token is expired")
    void validateAndExtractEmail_ShouldReturnEmpty_WhenTokenIsExpired() {
        JwtService expiredJwtService = new JwtService(testSecretKey, -1000);
        String expiredToken = expiredJwtService.generateToken(testEmail);

        Optional<String> result = jwtService.validateAndExtractEmail(expiredToken);

        assertTrue(result.isEmpty());
    }
}