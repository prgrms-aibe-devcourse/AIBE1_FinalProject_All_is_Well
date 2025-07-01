package org.example.bookmarket.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookmarket.chat.dto.ChatMessageRequest;
import org.example.bookmarket.chat.dto.ChatMessageResponse;
import org.example.bookmarket.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageRequest request) {
        log.info("📨 WebSocket 메시지 수신: {}", request);

        ChatMessageResponse response = chatService.sendMessage(request);

        messagingTemplate.convertAndSend("/topic/chat/" + request.getChannelId(), response);
    }
}