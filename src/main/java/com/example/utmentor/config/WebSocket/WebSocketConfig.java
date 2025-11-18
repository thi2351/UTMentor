package com.example.utmentor.config.WebSocket;

import com.example.utmentor.handler.WebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketHandler WebSocketHandler;

    public WebSocketConfig(WebSocketHandler chatWebSocketHandler) {
        this.WebSocketHandler = chatWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(WebSocketHandler, "/ws")
                .setAllowedOriginPatterns("http://localhost:5173");
    }
}