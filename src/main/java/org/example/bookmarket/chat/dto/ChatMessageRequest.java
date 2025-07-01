package org.example.bookmarket.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageRequest {
    private Long channelId;    // 메시지를 보낼 채널 ID
    private Long senderId;     // 보낸 사람
    private String content;    // 메시지 내용
}