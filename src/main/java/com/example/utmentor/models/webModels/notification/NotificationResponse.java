package com.example.utmentor.models.webModels.notification;

import com.example.utmentor.models.docEntities.Notification.NotificationType;

public record NotificationResponse
        (
        String id,
        String title,
        NotificationType type,
        String content,
        boolean isRead,
        String timestamp
        )
{ }
