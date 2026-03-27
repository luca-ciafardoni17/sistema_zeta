package com.platformzeta.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * These information are meant to be provided from a registration form from client application.
 * Anagraphic details are lost since right now (27/03) there are no Kafka message integrations.
 * @param email
 * @param password
 * @param mobileNumber
 * @param accountHolder
 * @param taxCode
 * @param country
 * @param province
 * @param town
 * @param address
 */
public record RegisterRequestDto(
        @Schema(example = "mario.rossi@aruba.it", description = "Email from login form")
        String email,
        @Schema(example = "iu21278y9p1swyp9ayp198uy1p9", description = "Hashed unreadable password")
        String password,
        @Schema(example = "398786265", description = "Mobile number")
        String mobileNumber,
        @Schema(example = "Normal person", description = "Type of user")
        String accountHolder,
        @Schema(example = "CFRLCU02H17E472N", description = "Tax code")
        String taxCode,
        @Schema(example = "Italy", description = "Country")
        String country,
        @Schema(example = "Latina", description = "Province")
        String province,
        @Schema(example = "Latina", description = "Town")
        String town,
        @Schema(example = "Via cona 8", description = "Address")
        String address
) {
}
