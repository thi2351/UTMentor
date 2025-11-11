package com.example.utmentor.metadata;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.utmentor.infrastructures.repository.Interface.RatingRepository;
import com.example.utmentor.models.docEntities.Rating;

/**
 * Component responsible for creating bootstrap reviews during initialization.
 * Contains all review data and creation logic.
 */
@Component
public class ReviewBootstrap {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private BootstrapHelper helper;

    /**
     * Data structure for a single review entry
     */
    private static class ReviewData {
        final String tutorId;
        final String reviewerId;
        final Integer rating;
        final String comment;
        final String timestamp;

        ReviewData(String tutorId, String reviewerId, Integer rating, String comment, String timestamp) {
            this.tutorId = tutorId;
            this.reviewerId = reviewerId;
            this.rating = rating;
            this.comment = comment;
            this.timestamp = timestamp;
        }
    }

    /**
     * Create all bootstrap reviews.
     */
    public void createBootstrapReviews() {
        try {
            System.out.println("Creating bootstrap reviews...");

            // Get review data
            List<ReviewData> tutor001Reviews = getTutor001Reviews();
            List<ReviewData> tutor003Reviews = getTutor003Reviews();
            List<ReviewData> tutor006Reviews = getTutor006Reviews();

            // Create reviews for all tutors
            createReviewsForTutor(tutor001Reviews);
            createReviewsForTutor(tutor003Reviews);
            createReviewsForTutor(tutor006Reviews);

            System.out.println("Successfully created bootstrap reviews!");
            System.out.println("  - tutor001: " + tutor001Reviews.size() + " reviews");
            System.out.println("  - tutor003: " + tutor003Reviews.size() + " reviews");
            System.out.println("  - tutor006: " + tutor006Reviews.size() + " reviews");

        } catch (Exception e) {
            System.err.println("Error creating bootstrap reviews: " + e.getMessage());
        }
    }

    /**
     * Get all bootstrap reviews for tutor001.
     * Tutor 1: tutor001 (Nguyễn Văn An) - AI/ML expert - 15 reviews
     */
    private List<ReviewData> getTutor001Reviews() {
        return List.of(
                new ReviewData("tutor001", "student001", 5, "Great mentor—clear roadmap and actionable feedback.", "2025-11-07T16:58:15+07:00"),
                new ReviewData("tutor001", "student002", 5, "Excellent AI/ML guidance. Helped me understand deep learning concepts clearly.", "2025-11-06T14:30:00+07:00"),
                new ReviewData("tutor001", "student003", 4, "Strong DSA guidance; would love more mock interviews.", "2025-11-05T10:15:00+07:00"),
                new ReviewData("tutor001", "student004", 5, "Explained complex topics simply; very responsive.", "2025-11-04T09:20:00+07:00"),
                new ReviewData("tutor001", "student005", 5, "Best mentor for machine learning. Very patient and knowledgeable.", "2025-11-03T15:45:00+07:00"),
                new ReviewData("tutor001", "student006", 4, "Good explanations but sometimes moves too fast.", "2025-11-02T11:30:00+07:00"),
                new ReviewData("tutor001", "student007", 5, "Outstanding support for my research project. Highly recommend!", "2025-11-01T13:20:00+07:00"),
                new ReviewData("tutor001", "student008", 5, "Clear explanations of neural networks and backpropagation.", "2025-10-31T16:10:00+07:00"),
                new ReviewData("tutor001", "student009", 4, "Helpful with data preprocessing and feature engineering.", "2025-10-30T10:00:00+07:00"),
                new ReviewData("tutor001", "student010", 5, "Great at explaining TensorFlow and PyTorch frameworks.", "2025-10-29T14:25:00+07:00"),
                new ReviewData("tutor001", "student011", 4, "Good mentor, but could provide more practice exercises.", "2025-10-28T09:15:00+07:00"),
                new ReviewData("tutor001", "student012", 5, "Excellent at breaking down complex ML algorithms.", "2025-10-27T11:40:00+07:00"),
                new ReviewData("tutor001", "student001", 5, "Second review: Continued to help me improve my skills significantly.", "2025-10-26T15:30:00+07:00"),
                new ReviewData("tutor001", "student002", 4, "Good follow-up sessions. Appreciate the detailed feedback.", "2025-10-25T10:20:00+07:00"),
                new ReviewData("tutor001", "student003", 5, "Best tutor I've had. Transformed my understanding of AI.", "2025-10-24T13:50:00+07:00")
        );
    }

    /**
     * Get all bootstrap reviews for tutor003.
     * Tutor 2: tutor003 (Lê Văn Cường) - Algorithms expert - 18 reviews
     */
    private List<ReviewData> getTutor003Reviews() {
        return List.of(
                new ReviewData("tutor003", "student001", 5, "Excellent problem-solving approach. Highly recommend!", "2025-11-07T16:58:15+07:00"),
                new ReviewData("tutor003", "student002", 5, "Best tutor for competitive programming preparation.", "2025-11-06T14:30:00+07:00"),
                new ReviewData("tutor003", "student003", 5, "Great at explaining algorithms step by step.", "2025-11-05T10:15:00+07:00"),
                new ReviewData("tutor003", "student004", 4, "Helped me improve my coding interview skills significantly.", "2025-11-04T09:20:00+07:00"),
                new ReviewData("tutor003", "student005", 5, "Master of data structures. Explained everything clearly.", "2025-11-03T15:45:00+07:00"),
                new ReviewData("tutor003", "student006", 5, "Excellent at teaching dynamic programming concepts.", "2025-11-02T11:30:00+07:00"),
                new ReviewData("tutor003", "student007", 4, "Good mentor, very knowledgeable about graph algorithms.", "2025-11-01T13:20:00+07:00"),
                new ReviewData("tutor003", "student008", 5, "Helped me solve LeetCode hard problems I couldn't crack.", "2025-10-31T16:10:00+07:00"),
                new ReviewData("tutor003", "student009", 5, "Best algorithms tutor. Made complex topics simple.", "2025-10-30T10:00:00+07:00"),
                new ReviewData("tutor003", "student010", 4, "Great explanations but sometimes too advanced for beginners.", "2025-10-29T14:25:00+07:00"),
                new ReviewData("tutor003", "student011", 5, "Excellent at teaching time complexity analysis.", "2025-10-28T09:15:00+07:00"),
                new ReviewData("tutor003", "student012", 5, "Helped me prepare for Google interview. Got the job!", "2025-10-27T11:40:00+07:00"),
                new ReviewData("tutor003", "student001", 5, "Continued mentorship helped me win programming contest.", "2025-10-26T15:30:00+07:00"),
                new ReviewData("tutor003", "student002", 4, "Good follow-up on previous sessions.", "2025-10-25T10:20:00+07:00"),
                new ReviewData("tutor003", "student003", 5, "Master of competitive programming. Learned so much!", "2025-10-24T13:50:00+07:00"),
                new ReviewData("tutor003", "student004", 4, "Helpful with algorithm optimization techniques.", "2025-10-23T09:30:00+07:00"),
                new ReviewData("tutor003", "student005", 5, "Best investment in my coding career.", "2025-10-22T14:15:00+07:00"),
                new ReviewData("tutor003", "student006", 5, "Transformed my problem-solving skills completely.", "2025-10-21T11:00:00+07:00")
        );
    }

    /**
     * Get all bootstrap reviews for tutor006.
     * Tutor 3: tutor006 (Võ Thị Phương) - Web Dev - 12 reviews
     */
    private List<ReviewData> getTutor006Reviews() {
        return List.of(
                new ReviewData("tutor006", "student001", 5, "Amazing full-stack guidance. Learned React and Spring Boot quickly.", "2025-11-07T16:58:15+07:00"),
                new ReviewData("tutor006", "student002", 5, "Best web development mentor. Helped me build my first full-stack app.", "2025-11-06T14:30:00+07:00"),
                new ReviewData("tutor006", "student003", 4, "Good web development mentor with modern practices.", "2025-11-05T10:15:00+07:00"),
                new ReviewData("tutor006", "student004", 5, "Excellent at teaching RESTful API design.", "2025-11-04T09:20:00+07:00"),
                new ReviewData("tutor006", "student005", 4, "Helpful with database design and optimization.", "2025-11-03T15:45:00+07:00"),
                new ReviewData("tutor006", "student006", 5, "Great at explaining React hooks and state management.", "2025-11-02T11:30:00+07:00"),
                new ReviewData("tutor006", "student007", 5, "Helped me understand microservices architecture.", "2025-11-01T13:20:00+07:00"),
                new ReviewData("tutor006", "student008", 4, "Good mentor, but could provide more real-world examples.", "2025-10-31T16:10:00+07:00"),
                new ReviewData("tutor006", "student009", 5, "Excellent guidance on TypeScript and modern JavaScript.", "2025-10-30T10:00:00+07:00"),
                new ReviewData("tutor006", "student010", 5, "Best full-stack tutor. Learned so much in short time.", "2025-10-29T14:25:00+07:00"),
                new ReviewData("tutor006", "student011", 4, "Helpful with deployment and DevOps practices.", "2025-10-28T09:15:00+07:00"),
                new ReviewData("tutor006", "student012", 5, "Transformed my web development skills completely.", "2025-10-27T11:40:00+07:00")
        );
    }

    /**
     * Create reviews for a specific tutor.
     */
    private void createReviewsForTutor(List<ReviewData> reviews) {
        for (ReviewData review : reviews) {
            createReview(
                    helper.generateReviewId(),
                    review.tutorId,
                    review.reviewerId,
                    review.rating,
                    review.comment,
                    helper.parseTimestamp(review.timestamp)
            );
        }
    }

    /**
     * Create a single review.
     */
    private void createReview(String reviewId, String revieweeID, String reviewerID,
                              Integer rating, String comment, Instant timestamp) {
        try {
            // Check if review already exists
            if (ratingRepository.existsById(reviewId)) {
                return;
            }

            Rating ratingEntity = new Rating(reviewId, revieweeID, reviewerID, rating, comment, timestamp);
            ratingRepository.save(ratingEntity);
        } catch (Exception e) {
            System.err.println("Error creating review " + reviewId + ": " + e.getMessage());
        }
    }
}

