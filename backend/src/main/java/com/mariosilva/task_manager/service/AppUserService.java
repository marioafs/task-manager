package com.mariosilva.task_manager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mariosilva.task_manager.document.AppUser;
import com.mariosilva.task_manager.dto.RegisterRequestDTO;
import com.mariosilva.task_manager.dto.AppUserResponseDTO;
import com.mariosilva.task_manager.enums.UserRole;
import com.mariosilva.task_manager.repository.AppUserRepository;

@Service
public class AppUserService {
    
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserService(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<AppUserResponseDTO> getAllUsers() {
        List<AppUserResponseDTO> users = new ArrayList<>();
        for (AppUser user : userRepository.findAll()) {
            users.add(convertToUserResponseDTO(user));
        }
        return users;
    }

    public Optional<AppUserResponseDTO> getUserById(String id) {
        return userRepository.findById(id).map(this::convertToUserResponseDTO);
    }

    public Optional<AppUserResponseDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(this::convertToUserResponseDTO);
    }

    public AppUserResponseDTO createUser(RegisterRequestDTO registerRequestDTO) {
        AppUser user = registerRequestDTOToUser(registerRequestDTO);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setRole(UserRole.USER);
        AppUser savedUser = userRepository.save(user);
        return convertToUserResponseDTO(savedUser);
    }

    
    public boolean deleteUser(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private AppUserResponseDTO convertToUserResponseDTO(AppUser user) {
        if (user == null) {
            return null;
        }
        AppUserResponseDTO dto = new AppUserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    private AppUser registerRequestDTOToUser(RegisterRequestDTO registerRequestDTO) {
        AppUser user = new AppUser();
        user.setFirstName(registerRequestDTO.getFirstName());
        user.setLastName(registerRequestDTO.getLastName());
        user.setEmail(registerRequestDTO.getEmail());
        
        String hashedPassword = passwordEncoder.encode(
            registerRequestDTO.getPassword()
        );

        user.setPasswordHash(hashedPassword);

        return user;
    }
}
