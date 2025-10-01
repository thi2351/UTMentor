package com.example.utmentor.models.docEntities.users;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;

import com.example.utmentor.models.docEntities.Department;
import com.example.utmentor.models.docEntities.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class User {
    @NotBlank @Indexed(unique = true)
    String id;

    @NotBlank
    String  firstName;

    @NotBlank
    String lastName;

    Department department;

    List<Role> roles = new ArrayList<>();

    String avatarUrl=null;

    @NotBlank @Email @Indexed(unique = true)
    String email;

    @NotBlank @Indexed(unique = true)
    String username;

    @NotBlank
    String passwordHash;

    StudentProfile studentProfile;

    TutorProfile tutorProfile;

    boolean isActive = false;

    boolean isDeleted = false;

    Instant createdAt = Instant.now();


    public User(String id,
                String firstName,
                String lastName,
                Department department,
                List<Role> roles,
                String email,
                String username,
                String avatarUrl,
                String passwordHash,
                StudentProfile studentProfile,
                TutorProfile tutorProfile) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.roles = roles != null ? new ArrayList<>(roles) : new ArrayList<>();
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.studentProfile = studentProfile;
        this.tutorProfile = tutorProfile;
        this.avatarUrl = avatarUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles != null ? new ArrayList<>(roles) : new ArrayList<>();
    }
    
    // Convenience methods for single role operations
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
    
    // Helper methods for role management
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
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public TutorProfile getTutorProfile() {
        return tutorProfile;
    }

    public void setTutorProfile(TutorProfile tutorProfile) {
        this.tutorProfile = tutorProfile;
    }

    public StudentProfile getStudentProfile() {
        return studentProfile;
    }

    public void setStudentProfile(StudentProfile studentProfile) {
        this.studentProfile = studentProfile;
    }
}
