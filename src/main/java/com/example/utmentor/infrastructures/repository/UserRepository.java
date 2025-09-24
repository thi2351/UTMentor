package com.example.utmentor.infrastructures.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.utmentor.models.docEntities.users.User;

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByUsername(String username);
    boolean existsByStudentEmail(String studentEmail);
    Optional<User> findByUsername(String username);
}
