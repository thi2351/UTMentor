package com.example.utmentor.models.webModels.otp;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendOtpRequest(
        @NotBlank @Email String email
) {
}
