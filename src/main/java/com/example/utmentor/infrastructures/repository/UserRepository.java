package com.example.utmentor.infrastructures.repository;

import com.example.utmentor.models.docEntities.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByUsername(String username);
    boolean existsByStudentID(String studentID);
    Optional<User> findByUsername(String username);
}
