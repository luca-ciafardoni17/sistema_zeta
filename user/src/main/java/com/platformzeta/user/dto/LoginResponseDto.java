package com.platformzeta.user.dto;

public record LoginResponseDto(
        String message,
        UserDto user,
        String jwtToken
) {
}
