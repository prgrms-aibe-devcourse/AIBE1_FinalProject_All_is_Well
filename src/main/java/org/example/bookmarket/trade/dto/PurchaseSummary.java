
package org.example.bookmarket.trade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseSummary {
    private Long transactionId;
    private String itemTitle;
    private String sellerNickname;
    private Integer price;
    private String status;
    private LocalDateTime updatedAt;
}
