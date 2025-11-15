package com.example.utmentor.models.docEntities.Notification;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    private String sendUserId;
    private String toUserId;
    private String content;

    private NotificationType type;
    boolean isRead;
    private Instant createdAt;
    private Instant updatedAt;


    public Notification() {}

    public Notification(String sendUserId, String toUserId, String content, NotificationType type) {
        this.sendUserId = sendUserId;
        this.toUserId = toUserId;
        this.content = content;
        this.type = type;
        this.isRead = false;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }


    public String getId() { return id; }
    public void setId(String id) {
        this.id = id;
        this.updatedAt = Instant.now();
    }

    public String getSendUserId() { return sendUserId; }
    public void setSendUserId(String sendUserId) {
        this.sendUserId = sendUserId;
        this.updatedAt = Instant.now();
    }

    public String getToUserId() { return toUserId; }
    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
        this.updatedAt = Instant.now();
    }

    public String getContent() { return content; }
    public void setContent(String content) {
        this.content = content;
        this.updatedAt = Instant.now();
    }


    public NotificationType getType() { return type; }
    public void setType(NotificationType type) {
        this.type = type;
        this.updatedAt = Instant.now();
    }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) {
        isRead = read;
        this.updatedAt = Instant.now();
    }

    public Instant getUpdatedAt() { return updatedAt; }

}