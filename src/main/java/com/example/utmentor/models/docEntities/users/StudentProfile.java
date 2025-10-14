package com.example.utmentor.models.docEntities.users;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;

@Document(collection = "studentProfiles")
public class StudentProfile {
    @Id
    @NotBlank
    private String id; // This will be the same as User.id (FK)
    
    @NotBlank
    private String studentID;
    
    private boolean isActive = true;
    
    private Instant createdAt = Instant.now();
    
    private Instant updatedAt = Instant.now();

    // Constructors
    public StudentProfile() {}

    public StudentProfile(String id, String studentID, boolean isActive) {
        this.id = id;
        this.studentID = studentID;
        this.isActive = isActive;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        this.updatedAt = Instant.now();
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
        this.updatedAt = Instant.now();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
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
