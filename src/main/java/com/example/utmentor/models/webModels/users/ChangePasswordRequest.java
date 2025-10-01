package com.example.utmentor.models.webModels.users;

public record ChangePasswordRequest(String currentPassword, String newPassword) {}