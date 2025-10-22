package com.example.utmentor.infrastructures.repository.Interface;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.utmentor.models.docEntities.users.TutorProfile;

@Repository
public interface TutorProfileRepository extends MongoRepository<TutorProfile, String> {
    // Basic CRUD operations
    List<TutorProfile> findByIsActiveTrue();
}


