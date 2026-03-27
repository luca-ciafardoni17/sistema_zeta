package com.platformzeta.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * jwtToken should be memorized in browser storage to make API calls from other microservices possible
 * @param message
 * @param user
 * @param jwtToken
 */
public record LoginResponseDto(
        @Schema(example = "500", description = "HTTP Status code")
        String message,
        UserDto user,
        @Schema(example = "eySNKIOp89197120....", description = "Very long JWT Token")
        String jwtToken
) {
}
