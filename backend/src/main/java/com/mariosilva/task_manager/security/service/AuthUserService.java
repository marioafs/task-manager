package com.mariosilva.task_manager.security.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mariosilva.task_manager.document.AppUser;
import com.mariosilva.task_manager.enums.UserRole;
import com.mariosilva.task_manager.repository.AppUserRepository;

@Service
public class AuthUserService implements UserDetailsService{

    private final AppUserRepository userRepository;

    public AuthUserService(AppUserRepository userRepository) {
        this.userRepository = userRepository; 
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByEmail(email)
            .orElseThrow(() ->
                new UsernameNotFoundException(
                    "User with email " + email + " not found"));     
                    
        String[] roles = (appUser.getRole() == UserRole.ADMIN) 
            ? new String[]{"ADMIN", "USER"} 
            : new String[]{"USER"};

        return User.withUsername(email)
            .password(appUser.getPasswordHash())
            .roles(roles)
            .build();
    }
    
}
