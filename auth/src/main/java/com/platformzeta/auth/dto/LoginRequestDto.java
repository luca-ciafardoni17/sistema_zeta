package com.platformzeta.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @param email
 * @param password
 */
public record LoginRequestDto(
        @Schema(example = "mario.rossi@aruba.it", description = "Email from login form")
        String email,
        @Schema(example = "securePassword@123", description = "Unhashed password from login form")
        String password
) {
}
