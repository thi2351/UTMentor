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

}
