package com.example.utmentor.models.webModels.datacore;

import com.example.utmentor.models.docEntities.Department;
import com.example.utmentor.models.docEntities.Role;
import com.example.utmentor.models.docEntities.users.StudentProfile;
import com.example.utmentor.models.docEntities.users.TutorProfile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateDatacoreRequest(
        @NotBlank String id,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull Department department,
        @NotNull Role role,
        @NotBlank @Email String email,
        StudentProfile studentProfile,
        TutorProfile tutorProfile
) {
}
