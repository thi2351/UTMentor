package com.example.utmentor.models.webModels.users;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {} 