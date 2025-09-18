package com.example.utmentor.models.docEntities;

import com.example.utmentor.models.webModels.users.CreateUserRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.UUID;

public class User {
    @NotBlank @Indexed(unique = true)
    String id;

    @NotBlank
    String  firstName;

    @NotBlank
    String lastName;

    Department department;

    Role role;

    @NotBlank
    String studentID;

    @NotBlank @Email @Indexed(unique = true)
    String studentEmail;

    @NotBlank @Indexed(unique = true)
    String username;

    @NotBlank
    String passwordHash;

    StudentProfile studentProfile;

    TutorProfile tutorProfile;

    public User(String id,
                String firstName,
                String lastName,
                Department department,
                Role role,
                String studentID,
                String studentEmail,
                String username,
                String passwordHash,
                StudentProfile studentProfile,
                TutorProfile tutorProfile) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.role = role;
        this.studentID = studentID;
        this.studentEmail = studentEmail;
        this.username = username;
        this.passwordHash = passwordHash;
        this.studentProfile = studentProfile;
        this.tutorProfile = tutorProfile;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
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
