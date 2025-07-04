package org.example.bookmarket.ai.service;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Part;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import org.example.bookmarket.ai.dto.PriceSuggestResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AiService {

    @Value("${gemini.project.id}")
    private String projectId;

    @Value("${gemini.location}")
    private String location;

    @Value("${gemini.vision.model.name}")
    private String modelName;

    public PriceSuggestResponse suggestPriceFromImage(String imageUrl, int newPrice) throws IOException {
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            String prompt = "당신은 중고책의 결함을 찾아내는 전문 분석가입니다. "
                    + "이 사진을 보고 아래 '결함 목록' 중에서 해당하는 것을 모두 찾아서 키워드 리스트 형태로 알려주세요. "
                    + "결함이 전혀 없으면 '없음'이라고 답해주세요. "
                    + "결함 목록: [모서리 닳음, 커버 긁힘, 페이지 찢어짐, 물에 젖은 자국, 낙서 또는 필기, 변색 또는 황변, 스티커 부착] "
                    + "결과는 반드시 '결함: [키워드1, 키워드2, ...]' 형식으로만 대답해주세요. "
                    + "예시) 결함: [모서리 닳음, 낙서 또는 필기]";

            // URL에서 이미지 데이터를 byte[]로 읽어옵니다
            byte[] imageData;
            try (InputStream inputStream = new URL(imageUrl).openStream()) {
                imageData = inputStream.readAllBytes();
            }

            Part imagePart = Part.newBuilder()
                    .setInlineData(
                            com.google.cloud.vertexai.api.Blob.newBuilder()
                                    .setMimeType("image/jpeg")
                                    .setData(com.google.protobuf.ByteString.copyFrom(imageData))
                                    .build()
                    )
                    .build();

            Part textPart = Part.newBuilder()
                    .setText(prompt)
                    .build();

            Content content = Content.newBuilder()
                    .addParts(imagePart)
                    .addParts(textPart)
                    .build();

            GenerativeModel model = new GenerativeModel(modelName, vertexAI);
            GenerateContentResponse response = model.generateContent(content);
            String analysisResultText = ResponseHandler.getText(response);

            List<String> defects = parseDefects(analysisResultText);
            int penalty = calculatePenalty(defects);
            double finalRatio = (100.0 - penalty) / 100.0;

            int suggestedMaxPrice = (int) (newPrice * finalRatio * 0.8 / 100.0) * 100;
            int suggestedMinPrice = (int) (suggestedMaxPrice * 0.85 / 100.0) * 100;

            return new PriceSuggestResponse(defects, suggestedMinPrice, suggestedMaxPrice);
        }
    }

    private List<String> parseDefects(String text) {
        Pattern pattern = Pattern.compile("결함: \\[(.*?)\\]");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String defectString = matcher.group(1).trim();
            if (defectString.equalsIgnoreCase("없음") || defectString.isEmpty()) {
                return Collections.emptyList();
            }
            return Arrays.stream(defectString.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
        return List.of("AI 응답 파싱 실패");
    }

    private int calculatePenalty(List<String> defects) {
        int totalPenalty = 0;
        for (String defect : defects) {
            switch (defect) {
            case "페이지 찢어짐":
            case "물에 젖은 자국":
                totalPenalty += 30;
                break;
            case "낙서 또는 필기":
            case "변색 또는 황변":
                totalPenalty += 20;
                break;
            case "모서리 닳음":
            case "커버 긁힘":
            case "스티커 부착":
                totalPenalty += 10;
                break;
            }
        }
        return Math.min(100, totalPenalty);
    }
}