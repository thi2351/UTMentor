package com.example.utmentor.infrastructures.repository;

import com.example.utmentor.models.docEntities.HCMUT_DATACORE.Datacore;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DatacoreRepository extends MongoRepository<Datacore, String> {
    boolean existsByStudentEmail(String studentEmail);
    Optional<Datacore> findByStudentEmail(String studentEmail);
}
