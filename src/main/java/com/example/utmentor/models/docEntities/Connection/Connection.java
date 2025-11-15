package com.example.utmentor.models.docEntities.Connection;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Connection entity representing a connection request between student and tutor.
 */
@Document(collection = "connections")
public class Connection {

    @Id
    private String id;
    private String tutorId;
    private String studentId;
    private StatusRequest status; // 'Accepted', 'Pending', 'Reject'
    private String message;
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Default constructor for Spring Data MongoDB.
     */
    public Connection() {}

    /**
     * Full constructor for creating connection instances.
     */
    public Connection(String id, String tutorId, String studentId, StatusRequest status, String message, Instant createdAt) {
        this.id = id;
        this.tutorId = tutorId;
        this.studentId = studentId;
        this.status = status;
        this.message = message;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        this.updatedAt = Instant.now();
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
        this.updatedAt = Instant.now();
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
        this.updatedAt = Instant.now();
    }

    public StatusRequest getStatus() {
        return status;
    }

    public void setStatus(StatusRequest status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        this.updatedAt = Instant.now();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "id='" + id + '\'' +
                ", tutorId='" + tutorId + '\'' +
                ", studentId='" + studentId + '\'' +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}