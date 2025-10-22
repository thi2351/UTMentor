package com.example.utmentor.infrastructures.repository.Interface;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.utmentor.models.docEntities.users.StudentProfile;

@Repository
public interface StudentProfileRepository extends MongoRepository<StudentProfile, String> {
    @Override
    Optional<StudentProfile> findById(String id);
    Optional<StudentProfile> findByStudentID(String studentID);
    List<StudentProfile> findByIsActiveTrue();
    @Override
    boolean existsById(String id);
    boolean existsByStudentID(String studentID);
}
