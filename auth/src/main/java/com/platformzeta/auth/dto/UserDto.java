package com.platformzeta.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Defined a class instead of record to allow empty field when creating
 */
@Getter @Setter
public class UserDto implements Serializable {
    @Schema(example = "1", description = "User id")
    private Long id;
    @Schema(example = "mario.rossi@aruba.it", description = "Email from login form")
    private String email;
    @Schema(example = "iu21278y9p1swyp9ayp198uy1p9", description = "Hashed unreadable password")
    private String password;
}