package com.example.utmentor.presentation.controllers;

import com.example.utmentor.models.docEntities.Notification.Notification;
import com.example.utmentor.models.docEntities.Notification.NotificationType;
import com.example.utmentor.models.webModels.connections.CreateConnectionRequest;

import com.example.utmentor.models.webModels.connections.CreateConnectionResponse;
import com.example.utmentor.services.ConnectionService;
import com.example.utmentor.services.NotificationService;
import jakarta.validation.Valid;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<CreateConnectionResponse> createConnection (
            @Valid @RequestBody CreateConnectionRequest request) throws IOException {

        CreateConnectionResponse response = connectionService.ConnectionService(request);
        Notification notification = new Notification(request.studentId(), request.tutorId(),
                "Bạn có một yêu cầu kết nối mới từ học sinh : " + request.message(), NotificationType.CONNECTION_REQUEST);
        notificationService.sendNotificationForNewConnection(notification);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/connections/getPendingReq")
    public ResponseEntity<?> getPendingRequests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var currentUserId = authentication.getName();
        Map<String, Object> response = new HashMap<>();
        response.put("data", connectionService.getPendingRequests(currentUserId));

        return ResponseEntity.ok(response);
    }

    @Getter
    @Setter
    public static class ConnectionHandleRequestDTO {
        private String connectionId;
        private String studentId;
        private String status;
    }

    @PostMapping("/connections/handle-request")
    public ResponseEntity<?> handleConnectionRequest(@RequestBody ConnectionHandleRequestDTO connectionHandleRequestDTO) throws IOException {
        String connectionId = connectionHandleRequestDTO.connectionId;
        String studentId = connectionHandleRequestDTO.studentId;
        String status = connectionHandleRequestDTO.status;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var currentUserId = authentication.getName();
        boolean isAccepted = status.equals("ACCEPTED");
        connectionService.handleConnectionRequest(connectionId, currentUserId, studentId, isAccepted);
        Notification notification = new Notification(currentUserId, studentId,
                "Yêu cầu kết nối của bạn đã được chấp nhận.", NotificationType.CONNECTION_REQUEST_RESPONSE);
        notificationService.sendNotificationForConnectionResponse(notification);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Đã " + (status == "ACCEPTED" ? "chấp nhận" : "từ chối" ) + " yêu cầu kết nối thành công.");
        return ResponseEntity.ok(response);
    }

}
