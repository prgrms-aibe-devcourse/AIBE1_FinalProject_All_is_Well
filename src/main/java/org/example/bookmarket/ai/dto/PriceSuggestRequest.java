package org.example.bookmarket.ai.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PriceSuggestRequest {
    private String imageUrl; // AI가 분석할 이미지의 S3 URL
    private int newPrice;    // 새 책 정가
}