package com.example.utmentor.models.webModels.notification;

public record NotificationResponse
        (
        String id,
        String title,
        String type,
        String content,
        boolean isRead,
        String timestamp
        )
{ }
