package com.example.utmentor.models.docEntities.users;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.utmentor.models.docEntities.Department;
import com.example.utmentor.models.docEntities.Role;

import jakarta.validation.constraints.NotBlank;

@Document(collection = "users")
public class User {
    @Id
    @NotBlank
    String id;

    @NotBlank
    String firstName;

    @NotBlank
    String lastName;

    Department department;

    List<Role> roles = new ArrayList<>();

    String avatarUrl = null;

    String description;

    // New field for phone number
    String phoneNumber;

    @NotBlank @Indexed(unique = true)
    String username;

    @NotBlank
    String passwordHash;

    boolean isActive = false;

    boolean isDeleted = false;

    Instant createdAt = Instant.now();

    Instant updatedAt = Instant.now();

    public User(String id,
                String firstName,
                String lastName,
                Department department,
                List<Role> roles,
                String username,
                String avatarUrl,
                String passwordHash) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.roles = roles != null ? new ArrayList<>(roles) : new ArrayList<>();
        this.username = username;
        this.passwordHash = passwordHash;
        this.avatarUrl = avatarUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.updatedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        this.updatedAt = Instant.now();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.updatedAt = Instant.now();
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
        this.updatedAt = Instant.now();
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles != null ? new ArrayList<>(roles) : new ArrayList<>();
        this.updatedAt = Instant.now();
    }

    public Role getPrimaryRole() {
        return roles.isEmpty() ? null : roles.get(0);
    }

    public void setPrimaryRole(Role role) {
        if (role != null) {
            this.roles = List.of(role);
        } else {
            this.roles = new ArrayList<>();
        }
    }

    public void addRole(Role role) {
        if (role != null && !roles.contains(role)) {
            roles.add(role);
        }
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    public boolean hasAnyRole(List<Role> requiredRoles) {
        return requiredRoles.stream().anyMatch(this.roles::contains);
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        this.updatedAt = Instant.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = Instant.now();
    }

    // New getter/setter for phoneNumber
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.updatedAt = Instant.now();
    }

    public String getEmail() {
        return username + "@hcmut.edu.vn";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        this.updatedAt = Instant.now();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        this.updatedAt = Instant.now();
    }

    public boolean hasTutorProfile() {
        return roles.contains(Role.TUTOR);
    }

    public boolean hasStudentProfile() {
        return roles.contains(Role.STUDENT);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
        this.updatedAt = Instant.now();
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        this.isDeleted = deleted;
        this.updatedAt = Instant.now();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        this.updatedAt = Instant.now();
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}