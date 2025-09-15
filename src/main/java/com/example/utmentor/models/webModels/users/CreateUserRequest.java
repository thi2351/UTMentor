package com.example.utmentor.models.webModels.users;

import com.example.utmentor.models.docEntities.Department;
import com.example.utmentor.models.docEntities.Role;
import jakarta.validation.constraints.*;

public record CreateUserRequest(
        @NotBlank String lastName,
        @NotBlank String firstName,
        @NotNull Department department,
        @Email @NotBlank String email,
        @NotBlank @Size(min = 3, max = 32)
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "username can contain letters, digits, dot, underscore, hyphen")
        String username,
        @NotBlank @Size(min = 8, max = 128)
        String password,
        @NotNull Role role
) {}