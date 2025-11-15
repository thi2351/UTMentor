package com.example.utmentor.models.webModels.connections;

import jakarta.validation.constraints.NotBlank;

public record CreateConnectionRequest(
        @NotBlank String tutorId,
        @NotBlank String studentId,
         String message
) {}

