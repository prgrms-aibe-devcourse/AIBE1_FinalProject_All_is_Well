package org.example.bookmarket.chat.controller;

import lombok.RequiredArgsConstructor;
import org.example.bookmarket.chat.dto.*;
import org.example.bookmarket.chat.service.ChatService;
import org.example.bookmarket.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller // ✅ 기존 @RestController에서 @Controller로 변경 (뷰 반환 메서드가 있기 때문)
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 📌 채널 생성
    @PostMapping("/channel")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody // JSON 응답 명시
    public ChatResponse createChannel(@RequestBody ChatRequest request) {
        return chatService.createChannel(request);
    }

    // 📌 유저의 채널 리스트 조회
    @GetMapping("/channels/{userId}")
    @ResponseBody
    public List<ChatResponse> getUserChannels(@PathVariable Long userId) {
        return chatService.getUserChannels(userId);
    }

    // 📌 특정 채널의 메시지 리스트 조회
    @GetMapping("/channel/{channelId}/messages")
    @ResponseBody
    public List<ChatMessageResponse> getMessages(@PathVariable Long channelId) {
        return chatService.getMessages(channelId);
    }

    // ✅ chatroom.html 반환 (WebSocket 채팅방 뷰)
    @GetMapping("/chat/room/{channelId}")
    public String getChatRoomPage(@PathVariable Long channelId,
                                  Model model,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        model.addAttribute("channelId", channelId);
        model.addAttribute("userId", userDetails.getUser().getId());
        return "chat/chatroom"; // 👉 templates/chat/chatroom.html 로 연결
    }
}