package com.platformzeta.user.service;

import com.platformzeta.user.config.security.JwtUtil;
import com.platformzeta.user.dto.LoginRequestDto;
import com.platformzeta.user.dto.LoginResponseDto;
import com.platformzeta.user.dto.RegisterRequestDto;
import com.platformzeta.user.dto.UserDto;
import com.platformzeta.user.entity.User;
import com.platformzeta.user.kafka.UserEventProducer;
import com.platformzeta.user.kafka.event.UserRegisteredEvent;
import com.platformzeta.user.repository.UserRepository;
import com.platformzeta.user.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserEventProducer userEventProducer;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerUser_Success() {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto(
                "mario@aruba.it",
                "securePassword123",
                "3898786265",
                null,
                null,
                null,
                null,
                null,
                null
        );
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("securePassword123")).thenReturn("encodedHash");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
        User result = userService.registerUser(registerRequestDto);
        assertThat(result.getEmail()).isEqualTo("mario@aruba.it");
        assertThat(result.getPasswordHash()).isEqualTo("encodedHash");
        verify(userRepository).save(any(User.class));
        verify(userEventProducer, times(1)).publishUserRegistered(any());
    }

    @Test
    void registerUser_WrongDomain_ThrowsException() {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto(
                "mario.rossi@gmail.com",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        assertThatThrownBy(() -> userService.registerUser(registerRequestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email domain must be aruba.it");
    }

    @Test
    void loginUser_Success() {
        LoginRequestDto request = new LoginRequestDto("mario@aruba.it", "password");
        Authentication mockAuth = mock(Authentication.class);
        User mockUser = new User();
        mockUser.setEmail("mario@aruba.it");
        mockUser.setPasswordHash("hashedPass");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuth);
        when(mockAuth.getPrincipal()).thenReturn(mockUser);
        when(jwtUtil.generateJwtToken(mockAuth)).thenReturn("fake-jwt-token");
        LoginResponseDto response = userService.loginUser(request);
        assertThat(response.jwtToken()).isEqualTo("fake-jwt-token");
        assertThat(response.user().getEmail()).isEqualTo("mario@aruba.it");
        verify(jwtUtil).generateJwtToken(mockAuth);
    }

    @Test
    void loginUser_InvalidCredentials_ThrowsException() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));
        LoginRequestDto request = new LoginRequestDto("wrong@aruba.it", "pass");
        assertThatThrownBy(() -> userService.loginUser(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid credentials");
    }
}
