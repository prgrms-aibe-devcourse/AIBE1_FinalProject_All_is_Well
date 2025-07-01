package org.example.bookmarket.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // 메시지를 구독할 경로
        config.setApplicationDestinationPrefixes("/app"); // 클라이언트가 메시지를 보낼 때의 prefix
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat") // WebSocket 연결 엔드포인트
                .setAllowedOriginPatterns("*") // CORS 허용
                .withSockJS(); // SockJS 지원
    }
}