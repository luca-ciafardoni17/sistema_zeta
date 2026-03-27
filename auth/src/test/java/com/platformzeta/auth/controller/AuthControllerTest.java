package com.platformzeta.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platformzeta.auth.dto.LoginRequestDto;
import com.platformzeta.auth.dto.LoginResponseDto;
import com.platformzeta.auth.dto.RegisterRequestDto;
import com.platformzeta.auth.dto.UserDto;
import com.platformzeta.auth.entity.User;
import com.platformzeta.auth.service.IAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private IAuthService userService;

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

    @Test
    void updateCredentials_ShouldReturnOk_WhenRequestPartsAreValid() throws Exception {
        String oldCredsJson = "{\"email\":\"old@zeta.com\",\"password\":\"oldPass\"}";
        String newCredsJson = "{\"email\":\"new@zeta.com\",\"password\":\"newPass\"}";
        LoginResponseDto response = new LoginResponseDto(
                "Credenziali aggiornate",
                new UserDto(),
                "new-jwt-token"
        );
        when(userService.updateCredentials(any(String.class), any(String.class))).thenReturn(response);
        mockMvc.perform(multipart(HttpMethod.PUT, "/user")
                        .file(new MockMultipartFile("oldCredentials", "", "application/json", oldCredsJson.getBytes()))
                        .file(new MockMultipartFile("newCredentials", "", "application/json", newCredsJson.getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Credenziali aggiornate"))
                .andExpect(jsonPath("$.jwtToken").value("new-jwt-token"));
    }

    @Test
    void deleteUser_ShouldReturnOk_WhenUserExists() throws Exception {
        LoginRequestDto credentials = new LoginRequestDto("mario.rossi@aruba.it", "securePassword@123");

        mockMvc.perform(delete("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(content().string("User mario.rossi@aruba.it deleted successfully"));
    }
}