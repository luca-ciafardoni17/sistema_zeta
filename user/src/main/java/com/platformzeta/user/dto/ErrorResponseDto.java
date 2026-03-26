package com.platformzeta.user.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        String apiPath,
        HttpStatus httpStatus,
        String message,
        LocalDateTime errorTime
) {
}
