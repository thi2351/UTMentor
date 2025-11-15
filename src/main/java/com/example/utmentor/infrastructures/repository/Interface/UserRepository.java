package com.example.utmentor.infrastructures.repository.Interface;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.utmentor.models.docEntities.Department;
import com.example.utmentor.models.docEntities.users.User;

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsById(String id);
    Optional<User> findByUsername(String username);


    List<User> findByIdIn(Iterable<String> ids);
    List<User> findByIdInAndDepartment(Iterable<String> ids, Department department);
    List<User> findByDepartment(Department department);
}
