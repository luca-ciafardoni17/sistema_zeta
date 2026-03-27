package com.platformzeta.user.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class UserDtoTest {

    @Test
    void testUserDtoGettersAndSetters() {
        UserDto dto = new UserDto();

        dto.setId(1L);
        dto.setEmail("mario@rossi.it");

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getEmail()).isEqualTo("mario@rossi.it");
    }
}