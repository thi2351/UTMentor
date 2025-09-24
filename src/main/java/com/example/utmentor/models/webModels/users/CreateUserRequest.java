package com.example.utmentor.models.webModels.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
        @NotBlank @Email String studentEmail,
        @NotBlank String password,
        @NotBlank String rewritePassword
) {

}
