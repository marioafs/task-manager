package com.mariosilva.task_manager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.mariosilva.task_manager.document.User;
import com.mariosilva.task_manager.dto.UserResponseDTO;
import com.mariosilva.task_manager.repository.UserRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponseDTO> getAllUsers() {
        List<UserResponseDTO> users = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            users.add(convertToDTO(user));
        }
        return users;
    }

    public Optional<UserResponseDTO> getUserById(String id) {
        return userRepository.findById(id).map(this::convertToDTO);
    }

    public Optional<UserResponseDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(this::convertToDTO);
    }

    public UserResponseDTO createUser(User user) {
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public Optional<UserResponseDTO> updateUser(String id, User userDetails) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setUsername(userDetails.getUsername());
            existingUser.setEmail(userDetails.getEmail());
            existingUser.setPasswordHash(userDetails.getPasswordHash());
            existingUser.setFirstName(userDetails.getFirstName());
            existingUser.setLastName(userDetails.getLastName());
            existingUser.setUpdatedAt(LocalDateTime.now());
            User updatedUser = userRepository.save(existingUser);
            return convertToDTO(updatedUser);
        });
    }

    public Optional<UserResponseDTO> patchUser(String id, User userDetails) {
        return userRepository.findById(id).map(existingUser -> {
            if (userDetails.getUsername() != null) {
                existingUser.setUsername(userDetails.getUsername());
            }
            if (userDetails.getEmail() != null) {
                existingUser.setEmail(userDetails.getEmail());
            }
            if (userDetails.getPasswordHash() != null) {
                existingUser.setPasswordHash(userDetails.getPasswordHash());
            }
            if (userDetails.getFirstName() != null) {
                existingUser.setFirstName(userDetails.getFirstName());
            }
            if (userDetails.getLastName() != null) {
                existingUser.setLastName(userDetails.getLastName());
            }
            existingUser.setUpdatedAt(LocalDateTime.now());
            User updatedUser = userRepository.save(existingUser);
            return convertToDTO(updatedUser);
        });
    }

    public boolean deleteUser(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private UserResponseDTO convertToDTO(User user) {
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
}
