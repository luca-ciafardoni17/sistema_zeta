package com.platformzeta.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * General purpose error response with meaningful fields
 * @param apiPath
 * @param httpStatus
 * @param message
 * @param errorTime
 */
public record ErrorResponseDto(
        @Schema(example = "/api/user/login", description = "Failed API call path")
        String apiPath,
        @Schema(example = "500", description = "HTTP Status code")
        HttpStatus httpStatus,
        @Schema(example = "Internal server error", description = "Status code message")
        String message,
        @Schema(example = "27-03-2026", description = "DD-MM-YYYY format data")
        LocalDateTime errorTime
) {
}
