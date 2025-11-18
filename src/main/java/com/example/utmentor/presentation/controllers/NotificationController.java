package com.example.utmentor.presentation.controllers;
import com.example.utmentor.services.NotificationService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class NotificationController {

    @Setter
    @Getter
    public static class MarkReadRequest {
        private String id;
    }

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/api/notifications/get")
    public ResponseEntity<?> getNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = authentication.getName();
        var notifications = notificationService.getNotificationsForCurrentUser(currentUserId);
        Map<String, Object> response = new HashMap<>();
        response.put("notifications", notifications);
        return ResponseEntity.ok(response);
    }


}
