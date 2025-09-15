package com.example.utmentor.models.docEntities;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Objects;

@Document("users")
public class User {

    @Id
    @Indexed(unique = true)
    private String id;

    @NotBlank
    private String lastName;

    @NotBlank
    private String firstName;

    @NotBlank
    private Department department;

    @Email
    @NotBlank
    @Indexed(unique = true)
    private String email;

    @NotBlank
    @Indexed(unique = true)
    private String username;

    @NotBlank
    private String passwordHash;

    @NotBlank
    private Role role;

    @NotBlank
    private Instant createdAt;

    private Instant lastModifiedAt = null;

    public User(String id,
                String lastName,
                String firstName,
                Department department,
                String email,
                String username,
                String passwordHash,
                Role role,
                Instant createdAt) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.department = department;
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
    }

    public String getLastName() {
        return lastName;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public Department getDepartment() {
        return department;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Role getRole() {
        return role;
    }

    public Instant getLastModifiedAt() {
        return lastModifiedAt;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof User u && Objects.equals(id, u.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}