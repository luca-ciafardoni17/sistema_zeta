package com.platformzeta.auth.dto;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class LoginResponseDtoTest {

    @Test
    void testLoginResponseDto() {
        UserDto user = new UserDto();
        user.setEmail("user@test.it");

        LoginResponseDto dto = new LoginResponseDto("Success", user, "jwt-token");

        assertThat(dto.message()).isEqualTo("Success");
        assertThat(dto.user().getEmail()).isEqualTo("user@test.it");
        assertThat(dto.jwtToken()).isEqualTo("jwt-token");
    }

}
