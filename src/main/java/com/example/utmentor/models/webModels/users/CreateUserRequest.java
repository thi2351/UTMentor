package com.example.utmentor.models.webModels.users;

import jakarta.validation.constraints.*;

public record CreateUserRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String studentID,
        //Validation
        @NotBlank @Email String studentEmail,
        //
        @NotBlank String username,
        //
        @NotBlank String password,
        //
        @NotBlank String rewritePassword
) {

}
