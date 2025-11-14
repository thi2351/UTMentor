package com.example.utmentor.models.docEntities;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Document(collection = "ratings")
public class Rating {
    @Id
    @NotBlank
    private String id;

    @NotBlank
    private String revieweeID; // Tutor being reviewed

    @NotBlank
    private String reviewerID; // Student who wrote the review

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    @NotBlank
    private String comment;

    @NotNull
    private Instant timestamp = Instant.now();

    public Rating() {}

    public Rating(String id, String revieweeID, String reviewerID, Integer rating, String comment) {
        this.id = id;
        this.revieweeID = revieweeID;
        this.reviewerID = reviewerID;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = Instant.now();
    }

    public Rating(String id, String revieweeID, String reviewerID, Integer rating, String comment, Instant timestamp) {
        this.id = id;
        this.revieweeID = revieweeID;
        this.reviewerID = reviewerID;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRevieweeID() {
        return revieweeID;
    }

    public void setRevieweeID(String revieweeID) {
        this.revieweeID = revieweeID;
    }

    public String getReviewerID() {
        return reviewerID;
    }

    public void setReviewerID(String reviewerID) {
        this.reviewerID = reviewerID;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}

