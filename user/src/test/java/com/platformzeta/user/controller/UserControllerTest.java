package com.platformzeta.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platformzeta.user.dto.LoginRequestDto;
import com.platformzeta.user.dto.LoginResponseDto;
import com.platformzeta.user.dto.RegisterRequestDto;
import com.platformzeta.user.dto.UserDto;
import com.platformzeta.user.entity.User;
import com.platformzeta.user.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private IUserService userService;

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) {
            return http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .build();
        }
    }

    @Test
    void apiLogin_ShouldReturnSuccess_WhenCredentialsAreCorrect() throws Exception {
        LoginRequestDto request = new LoginRequestDto("mario.rossi@aruba.it", "securePassword123");
        UserDto userDetails = new UserDto();
        RegisterRequestDto registrationDto = new RegisterRequestDto(
                "mario.rossi@aruba.it",
                null,
                null,
                "Mario Rossi",
                null,
                null,
                null,
                null,
                null
        );
        LoginResponseDto response = new LoginResponseDto(
                "Login effettuato con successo",
                userDetails,
                "eyJhbGciOiJIUzI1NiJ...ecc...ecc..."
        );
        when(userService.loginUser(any(LoginRequestDto.class))).thenReturn(response);
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login effettuato con successo"));
    }

    @Test
    void apiRegister_ShouldReturnCreated_WhenUserDtoIsValid() throws Exception {
        RegisterRequestDto registrationDto = new RegisterRequestDto(
                "new@platformzeta.com",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        User mockSavedUser = new User();
        mockSavedUser.setEmail("new@platformzeta.com");
        when(userService.registerUser(any(RegisterRequestDto.class))).thenReturn(mockSavedUser);
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User with email new@platformzeta.com created successfully!"));
    }
}