package com.example.utmentor.infrastructures.repository.Interface;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.utmentor.models.docEntities.Rating;

@Repository
public interface RatingRepository extends MongoRepository<Rating, String> {
    List<Rating> findByRevieweeID(String revieweeID, Sort sort);
    Page<Rating> findByRevieweeID(String revieweeID, Pageable pageable);
}

