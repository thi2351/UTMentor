package com.example.utmentor.infrastructures.repository.Interface;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.utmentor.models.docEntities.HCMUT_DATACORE.Datacore;

public interface DatacoreRepository extends MongoRepository<Datacore, String> {
    boolean existsByEmail(String email);
    Optional<Datacore> findByEmail(String email);
}
