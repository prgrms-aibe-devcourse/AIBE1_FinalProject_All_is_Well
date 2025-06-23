
package org.example.bookmarket.usedbook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsedBookSummary {
    private Long usedBookId;
    private String title;
    private Integer price;
    private String status;
    private String buyerNickname;
    private LocalDateTime updatedAt;
}
