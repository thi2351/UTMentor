package com.example.utmentor.models.docEntities.users;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.utmentor.models.docEntities.Expertise;

import jakarta.validation.constraints.NotBlank;

@Document(collection = "tutorProfiles")
public class TutorProfile {
    @Id
    @NotBlank
    private String id; // This will be the same as User.id (FK)
    
    private List<Expertise> expertise;
    
    private boolean isActive = true;
    
    private Integer maximumCapacity;
    
    private Integer currentMenteeCount = 0;
    
    private Integer ratingCount = 0;
    
    private Double ratingAvg = 0.0;
    
    private Instant createdAt = Instant.now();
    
    private Instant updatedAt = Instant.now();

    // Constructors
    public TutorProfile() {}

    public TutorProfile(String id, List<Expertise> expertise, 
                       boolean isActive, Integer maximumCapacity, Integer currentMenteeCount) {
        this.id = id;
        this.expertise = expertise;
        this.isActive = isActive;
        this.maximumCapacity = maximumCapacity;
        this.currentMenteeCount = currentMenteeCount;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        this.updatedAt = Instant.now();
    }

    public List<Expertise> getExpertise() {
        return expertise;
    }

    public void setExpertise(List<Expertise> expertise) {
        this.expertise = expertise;
        this.updatedAt = Instant.now();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
        this.updatedAt = Instant.now();
    }

    public Integer getMaximumCapacity() {
        return maximumCapacity;
    }

    public void setMaximumCapacity(Integer maximumCapacity) {
        this.maximumCapacity = maximumCapacity;
        this.updatedAt = Instant.now();
    }

    public Integer getCurrentMenteeCount() {
        return currentMenteeCount;
    }

    public void setCurrentMenteeCount(Integer currentMenteeCount) {
        this.currentMenteeCount = currentMenteeCount;
        this.updatedAt = Instant.now();
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount != null ? ratingCount : 0;
        this.updatedAt = Instant.now();
    }

    public Double getRatingAvg() {
        return ratingAvg;
    }

    public void setRatingAvg(Double ratingAvg) {
        this.ratingAvg = ratingAvg != null ? ratingAvg : 0.0;
        this.updatedAt = Instant.now();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}