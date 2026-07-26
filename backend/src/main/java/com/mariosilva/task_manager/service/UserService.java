package com.mariosilva.task_manager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mariosilva.task_manager.document.User;
import com.mariosilva.task_manager.dto.RegisterRequestDTO;
import com.mariosilva.task_manager.dto.UserResponseDTO;
import com.mariosilva.task_manager.repository.UserRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponseDTO> getAllUsers() {
        List<UserResponseDTO> users = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            users.add(convertToUserResponseDTO(user));
        }
        return users;
    }

    public Optional<UserResponseDTO> getUserById(String id) {
        return userRepository.findById(id).map(this::convertToUserResponseDTO);
    }

    public Optional<UserResponseDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(this::convertToUserResponseDTO);
    }

    public UserResponseDTO createUser(RegisterRequestDTO registerRequestDTO) {
        User user = registerRequestDTOToUser(registerRequestDTO);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return convertToUserResponseDTO(savedUser);
    }

    
    public boolean deleteUser(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private UserResponseDTO convertToUserResponseDTO(User user) {
        if (user == null) {
            return null;
        }
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    private User registerRequestDTOToUser(RegisterRequestDTO registerRequestDTO) {
        User user = new User();
        user.setFirstName(registerRequestDTO.getFirstName());
        user.setLastName(registerRequestDTO.getLastName());
        user.setEmail(registerRequestDTO.getEmail());
        user.setUsername(registerRequestDTO.getUsername());
        
        String hashedPassword = passwordEncoder.encode(
            registerRequestDTO.getPassword()
        );

        user.setPasswordHash(hashedPassword);

        return user;
    }
}
