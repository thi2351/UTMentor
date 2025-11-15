package com.example.utmentor.presentation.controllers;

import com.example.utmentor.models.docEntities.Notification.Notification;
import com.example.utmentor.models.docEntities.Notification.NotificationType;
import com.example.utmentor.models.webModels.connections.CreateConnectionRequest;

import com.example.utmentor.models.webModels.connections.CreateConnectionResponse;
import com.example.utmentor.services.ConnectionService;
import com.example.utmentor.services.NotificationService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ConnectionController {
    private final ConnectionService connectionService;
    private final NotificationService notificationService;
    public ConnectionController(ConnectionService connectionService, NotificationService notificationService) {
        this.connectionService = connectionService;
        this.notificationService = notificationService;
    }
    @PostMapping("/connections/create")
    public ResponseEntity<CreateConnectionResponse> createConnection(
            @Valid @RequestBody CreateConnectionRequest request) {

        CreateConnectionResponse response = connectionService.ConnectionService(request);
        Notification notification = new Notification(request.studentId(), request.tutorId(),
                "Bạn có một yêu cầu kết nối mới từ học sinh : " + request.message(), NotificationType.CONNECTION_REQUEST);
        notificationService.sendNotificationForNewConnection(notification);

        return ResponseEntity.ok(response);
    }
}
