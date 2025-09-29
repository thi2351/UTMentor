package com.example.utmentor.models.webModels.users;

public record LoginResponse(
        UserRespone user,
        String accessToken
) {} 