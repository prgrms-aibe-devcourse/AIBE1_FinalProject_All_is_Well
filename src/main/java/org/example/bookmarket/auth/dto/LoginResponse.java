package org.example.bookmarket.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(description = "발급된 Access Token")
        String accessToken,

        @Schema(description = "발급된 Refresh Token")
        String refreshToken
) {}