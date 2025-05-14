package com.example.vacation_reservation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메시지 브로커를 사용하여 클라이언트에 메시지를 전송
        config.enableSimpleBroker("/topic");  // 클라이언트가 /topic으로 시작하는 경로를 구독할 수 있게 설정
        config.setApplicationDestinationPrefixes("/app");  // 클라이언트가 /app으로 시작하는 경로로 메시지를 보낼 수 있게 설정
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 연결을 위한 엔드포인트 설정
        registry.addEndpoint("/notifications")
                .setAllowedOrigins("http://localhost:8081")
                .withSockJS();  // /notifications 엔드포인트로 WebSocket 연결
    }
}
