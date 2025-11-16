package com.example.utmentor.services;


import com.example.utmentor.infrastructures.repository.Interface.NotificationRepository;
import com.example.utmentor.models.docEntities.Notification.Notification;
import com.example.utmentor.models.webModels.notification.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotificationForNewConnection(Notification notification) {

        notificationRepository.save(notification);

        var tutorId = notification.getToUserId();

        NotificationResponse response = new NotificationResponse(
                notification.getId(),
                "Yêu cầu kết nối mới",
                notification.getType(),
                notification.getContent(),
                notification.isRead(),
                notification.getUpdatedAt().toString()
        );

        messagingTemplate.convertAndSendToUser(tutorId,"/queue/notifications", response);
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
    public void markAsRead(String notificationId) {
        notificationRepository.markAsRead(notificationId);
    }
}