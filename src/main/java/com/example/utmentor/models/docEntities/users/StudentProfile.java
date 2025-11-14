package com.example.utmentor.models.docEntities.users;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.utmentor.models.docEntities.Expertise;
import com.example.utmentor.models.webModels.profile.Achievement;

import jakarta.validation.constraints.NotBlank;

@Document(collection = "studentProfiles")
public class StudentProfile {
    @Id
    @NotBlank
    private String id;

    @NotBlank
    private String studentID;

    private Double currentGPA;

    private String studentDescription;

    private List<String> learningGoal;

    private List<Achievement> achievements;

    private List<Expertise> demandCourse;

    private boolean isActive = true;

    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();

    public StudentProfile() {}

    public StudentProfile(String id, String studentID, boolean isActive) {
        this.id = id;
        this.studentID = studentID;
        this.isActive = isActive;
    }

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

    public Double getCurrentGPA() {
        return currentGPA;
    }

    public void setCurrentGPA(Double currentGPA) {
        this.currentGPA = currentGPA;
        this.updatedAt = Instant.now();
    }

    public String getStudentDescription() {
        return studentDescription;
    }

    public void setStudentDescription(String studentDescription) {
        this.studentDescription = studentDescription;
        this.updatedAt = Instant.now();
    }

    public List<String> getLearningGoal() {
        return learningGoal;
    }

    public void setLearningGoal(List<String> learningGoal) {
        this.learningGoal = learningGoal;
        this.updatedAt = Instant.now();
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
        this.updatedAt = Instant.now();
    }

    public List<Expertise> getDemandCourse() {
        return demandCourse;
    }

    public void setDemandCourse(List<Expertise> demandCourse) {
        this.demandCourse = demandCourse;
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