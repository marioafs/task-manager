package com.mariosilva.task_manager.security.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;


@Service
public class JwtService {

    private final Algorithm algorithm;
    private final long expiration;

    public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expiration) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.expiration = expiration;
    }

    public String generateToken(String email) {

        try {
            return JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(algorithm);            
        } catch (JWTCreationException e) {
            throw new RuntimeException("Error while generating token", e);
        }
        
    }

    /**
     * Verifies the JWT token and extracts the email from the payload (subject).
     *
     * @param token the JWT string to verify (without the "Bearer " prefix)
     * @return an {@link Optional} containing the email if valid; {@link Optional#empty()} otherwise
     */
    public Optional<String> validateAndExtractEmail(String token) {

        try {
            String email = JWT.require(algorithm)
                .build()
                .verify(token) 
                .getSubject();

            return Optional.ofNullable(email);
            
        } catch (JWTVerificationException e) {
            return Optional.empty();
        }
         
    }

}