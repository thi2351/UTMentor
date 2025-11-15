package com.example.utmentor.presentation.controllers;


import com.example.utmentor.models.docEntities.Notification.Notification;
import com.example.utmentor.services.NotificationService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private  SimpMessagingTemplate messagingTemplate;

}
