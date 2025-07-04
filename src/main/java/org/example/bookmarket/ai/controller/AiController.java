package org.example.bookmarket.ai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookmarket.ai.dto.PriceSuggestRequest;
import org.example.bookmarket.ai.dto.PriceSuggestResponse;
import org.example.bookmarket.ai.service.AiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/suggest-price-from-image")
    public ResponseEntity<PriceSuggestResponse> suggestPrice(@RequestBody PriceSuggestRequest request) {
        if (request.getImageUrl() == null || request.getImageUrl().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            PriceSuggestResponse response = aiService.suggestPriceFromImage(request.getImageUrl(), request.getNewPrice());
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("AI 이미지 분석 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}