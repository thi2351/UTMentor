package com.example.utmentor.services;


import com.example.utmentor.infrastructures.repository.Interface.NotificationRepository;
import com.example.utmentor.models.docEntities.Notification.Notification;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotificationForNewConnection(Notification notification) {

        notificationRepository.save(notification);

        var tutorId = notification.getToUserId();

        Map<String, Object> response = new HashMap<>();

        response.put("id", notification.getId());
        response.put("title", "Yêu cầu kết nối mới");
        response.put("type", notification.getType());
        response.put("content", notification.getContent());
        response.put("sendUserId", notification.getSendUserId());
        response.put("isRead", notification.isRead());
        response.put("timestamp", notification.getUpdatedAt().toString());

        messagingTemplate.convertAndSendToUser(tutorId,"/queue/notifications", response);
    }
}