package com.example.utmentor.models.webModels.otp;

public record VerifyEmailResponse(String message, boolean success) {
    public VerifyEmailResponse(boolean success) {
        this(success ? "Verify successfully"
                        : "OTP code is not valid or is expire",
                success);
    }
}