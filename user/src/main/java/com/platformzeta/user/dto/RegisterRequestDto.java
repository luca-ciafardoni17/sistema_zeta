package com.platformzeta.user.dto;

public record RegisterRequestDto(
        String email,
        String password,
        String mobileNumber,
        String accountHolder,
        String taxCode,
        String country,
        String province,
        String town,
        String address
) {
}
