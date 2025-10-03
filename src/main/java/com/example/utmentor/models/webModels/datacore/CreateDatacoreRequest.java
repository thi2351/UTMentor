package com.example.utmentor.models.webModels.datacore;

import com.example.utmentor.models.docEntities.Department;
import com.example.utmentor.models.docEntities.Role;
import com.example.utmentor.models.docEntities.users.StudentProfile;
import com.example.utmentor.models.docEntities.users.TutorProfile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
public record CreateDatacoreRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull Department department,
        @NotNull List<Role> role,
        @NotBlank @Email String email,
        StudentProfile studentProfile,
        TutorProfile tutorProfile
) {
}
