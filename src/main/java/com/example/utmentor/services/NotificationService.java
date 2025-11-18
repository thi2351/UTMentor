package com.example.utmentor.services;


import com.example.utmentor.handler.WebSocketHandler;
import com.example.utmentor.infrastructures.repository.Interface.NotificationRepository;
import com.example.utmentor.models.docEntities.Notification.Notification;
import com.example.utmentor.models.webModels.notification.NotificationResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final WebSocketHandler handler;

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public void sendNotificationForNewConnection(Notification notification) throws IOException {

        CompletableFuture<Void> sendMessageFuture = CompletableFuture.runAsync(()->{
        var tutorId = notification.getToUserId();

        NotificationResponse response = new NotificationResponse(
                notification.getId(),
                "Yêu cầu kết nối mới",
                notification.getType(),
                notification.getContent(),
                notification.isRead(),
                notification.getUpdatedAt().toString()
        );

        var sendUserId = notification.getSendUserId();

        var  ReceiveSession = handler.getSessions().get(tutorId);

        if (ReceiveSession != null && ReceiveSession.isOpen()) {
            try {
                handler.sendMessage(ReceiveSession, Map.of(
                    "type", "PRIVATE_NOTIFICATION",
                    "to" , tutorId,
                    "from" , sendUserId,
                    "content", response));
            } catch (IOException e) {
                logger.error("Failed to send notification via WebSocket", e);
            }
        }
        });

        CompletableFuture<Void> saveDbFuture = CompletableFuture.runAsync(() -> {
            notificationRepository.save(notification);
        });
    }

    public List<NotificationResponse> getNotificationsForCurrentUser(String ToUserId) {

        List<Notification> notifications = notificationRepository.findByToUserId(ToUserId);

        List<NotificationResponse> responseList = notifications.stream().map(notification -> new NotificationResponse(
                notification.getId(),
                "Thông báo",
                notification.getType(),
                notification.getContent(),
                notification.isRead(),
                notification.getUpdatedAt().toString()
        )).toList();

        return responseList;
    }

}