package com.platformzeta.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

public class ErrorResponseDtoTest {

    @Test
    void testErrorResponseDto() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponseDto dto = new ErrorResponseDto("/api/login", HttpStatus.BAD_REQUEST, "Invalid", now);

        assertThat(dto.apiPath()).isEqualTo("/api/login");
        assertThat(dto.httpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(dto.message()).isEqualTo("Invalid");
        assertThat(dto.errorTime()).isEqualTo(now);
    }

}


