package com.platformzeta.user.dto;

public record LoginRequestDto(
        String email,
        String password
) {
}
