package com.mariosilva.task_manager.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mariosilva.task_manager.document.User;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}