package com.mariosilva.task_manager.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mariosilva.task_manager.document.AppUser;

public interface AppUserRepository extends MongoRepository<AppUser, String> {
    Optional<AppUser> findByEmail(String email);
}