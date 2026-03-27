package com.platformzeta.auth.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class LoginRequestDtoTest {

    @Test
    void testLoginRequestDto() {
        LoginRequestDto dto = new LoginRequestDto("test@test.it", "password");
        assertThat(dto.email()).isEqualTo("test@test.it");
        assertThat(dto.password()).isEqualTo("password");
    }

}