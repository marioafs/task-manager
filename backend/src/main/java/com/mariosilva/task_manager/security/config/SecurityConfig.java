package com.mariosilva.task_manager.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.mariosilva.task_manager.security.service.AuthUserService;

@Configuration
public class SecurityConfig {

    private final AuthUserService userDeatailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(AuthUserService userDeatailsService, PasswordEncoder passwordEncoder) {
        this.userDeatailsService = userDeatailsService;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDeatailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;        
    }

    @Bean
    public AuthenticationManager autheticationManager(AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }

}
