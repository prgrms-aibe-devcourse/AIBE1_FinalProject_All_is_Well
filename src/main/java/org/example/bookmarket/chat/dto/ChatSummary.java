
package org.example.bookmarket.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSummary {
    private Long channelId;
    private String lastMessage;
    private String partnerNickname;
    private LocalDateTime updatedAt;
}
