package org.example.bookmarket.chat.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageResponse {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long messageId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long senderId;

    private String content;
    private Boolean isRead;
    private LocalDateTime sentAt;
}