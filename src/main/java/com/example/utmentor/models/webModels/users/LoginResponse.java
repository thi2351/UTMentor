package com.example.utmentor.models.webModels.users;

public record LoginResponse(
        String username,
        String role,
        String fullName,
        String token
) {} 