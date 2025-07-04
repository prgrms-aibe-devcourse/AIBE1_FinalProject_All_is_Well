package org.example.bookmarket.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class PriceSuggestResponse {
    private List<String> detectedDefects; // AI가 찾아낸 결함 키워드 리스트
    private int suggestedMinPrice;        // 추천 최저가
    private int suggestedMaxPrice;        // 추천 최고가
}