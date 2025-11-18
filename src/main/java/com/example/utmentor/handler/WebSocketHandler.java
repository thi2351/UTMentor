package com.example.utmentor.handler;

import com.example.utmentor.infrastructures.repository.Interface.NotificationRepository;
import com.example.utmentor.models.webModels.notification.NotificationResponse;
import com.example.utmentor.services.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

import java.net.URI;
import java.util.List;
import java.util.Map;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Setter
@Getter
public class WebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private final Map<String, Set<String>> communityMembers = new ConcurrentHashMap<>();

    private final Map<String, Set<String>> userCommunities = new ConcurrentHashMap<>();

    @Autowired
    private NotificationRepository notificationRepository;


    private String getUserId(WebSocketSession session){
        URI uri = session.getUri();
        if (uri != null) {
            String query = session.getUri().getQuery();
            if (query != null && query.contains("userId=")) {
                String[] parmas = query.split("&");
                for (String param : parmas) {
                    if (param.contains("userId=")) {
                        return param.split("=")[1];
                    }
                }
            }
        }
        return session.getId();

    }

    public void sendMessage (WebSocketSession session, Map<String, Object> data) throws IOException {
        String message = objectMapper.writeValueAsString(data);
        session.sendMessage(new TextMessage(message));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserId(session);

        if (userId == null || userId.isBlank() || userId.equals("undefined")) {
            System.out.println("Closing invalid WebSocket session, userId=" + userId);
            session.close();
            return;
        }

        sessions.put(userId, session);
        userCommunities.putIfAbsent(userId, ConcurrentHashMap.newKeySet());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
        Map<String, Object> data = objectMapper.readValue(payload, Map.class);
        String type = (String) data.get("type");
        switch (type) {
            case "MARK_NOTIFICATIONS_AS_READ":
                notificationRepository.markAsRead((List<String>) data.get("ids"));
                break;
            case "JOIN_COMMUNITY":
                handleJoinCommunity(session, data);
                break;
            case "LEAVE_COMMUNITY":
                handleLeaveCommunity(session, data);
                break;

            default:
        }
    }

    private void handleLeaveCommunity(WebSocketSession session, Map<String, Object> data) throws IOException {
        String userId = getUserId(session);
        String communityId = (String) data.get("communityId");
        if ( communityId == null || communityId.isEmpty() ) {
            sendMessage(session, Map.of(
                        "type", "ERROR",
                        "message", "communityId is required"));
            return;
        }
        Set<String> members = communityMembers.get(communityId);
        if ( members != null ) {

            members.remove(userId);

            Set<String> communities = userCommunities.get(userId);
            if ( communities != null ) {
                communities.remove(communityId);
            }
            System.out.println("User " + userId + " left community: " + communityId);
        }

    }

    private void handleJoinCommunity(WebSocketSession session, Map<String, Object> data) throws IOException {
        String userId = getUserId(session);
        String communityId = (String) data.get("communityId");
        if (communityId == null || communityId.isEmpty()) {
           sendMessage(session, Map.of(
                   "type", "ERROR",
                   "message", "communityId is required"));
              return;
        }
        communityMembers.putIfAbsent(communityId, ConcurrentHashMap.newKeySet());
        communityMembers.get(communityId).add(userId);

        userCommunities.putIfAbsent(userId, ConcurrentHashMap.newKeySet());
        userCommunities.get(userId).add(communityId);

        sendMessage(session, Map.of(
                "type", "JOINED_COMMUNITY",
                "communityId", communityId
        ));
    }
}
