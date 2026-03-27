package com.platformzeta.auth.service;

import com.platformzeta.auth.config.security.JwtUtil;
import com.platformzeta.auth.dto.LoginRequestDto;
import com.platformzeta.auth.dto.LoginResponseDto;
import com.platformzeta.auth.dto.RegisterRequestDto;
import com.platformzeta.auth.entity.User;
import com.platformzeta.auth.repository.AuthRepository;
import com.platformzeta.auth.service.impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthRepository authRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl userService;

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
        when(authRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("securePassword123")).thenReturn("encodedHash");
        when(authRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
        User result = userService.registerUser(registerRequestDto);
        assertThat(result.getEmail()).isEqualTo("mario@aruba.it");
        assertThat(result.getPasswordHash()).isEqualTo("encodedHash");
        verify(authRepository).save(any(User.class));
        // verify(userEventProducer, times(1)).publishUserRegistered(any());
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

    @Test
    void updateCredentials_Success() {
        String oldEmail = "old@aruba.it";
        String newEmail = "new@aruba.it";
        String oldJson = "{\"email\":\"" + oldEmail + "\", \"password\":\"oldPass\"}";
        String newJson = "{\"email\":\"" + newEmail + "\", \"password\":\"newPass\"}";
        User existingUser = new User();
        existingUser.setEmail(oldEmail);
        existingUser.setPasswordHash("hashedOldPass");
        Authentication mockAuth = mock(Authentication.class);
        when(authRepository.findByEmail(oldEmail)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("oldPass", "hashedOldPass")).thenReturn(true);
        when(authRepository.findByEmail(newEmail)).thenReturn(Optional.empty()); // Nuova email libera
        when(passwordEncoder.encode("newPass")).thenReturn("hashedNewPass");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuth);
        when(jwtUtil.generateJwtToken(mockAuth)).thenReturn("new-jwt-token");
        LoginResponseDto response = userService.updateCredentials(oldJson, newJson);
        assertThat(response.jwtToken()).isEqualTo("new-jwt-token");
        assertThat(existingUser.getEmail()).isEqualTo(newEmail);
        verify(authRepository).save(existingUser);
    }

    @Test
    void updateCredentials_WrongPassword_ThrowsException() {
        String oldJson = "{\"email\":\"test@aruba.it\", \"password\":\"wrong\"}";
        String newJson = "{\"email\":\"new@aruba.it\", \"password\":\"new\"}";
        User existingUser = new User();
        existingUser.setPasswordHash("hashedPass");
        when(authRepository.findByEmail("test@aruba.it")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrong", "hashedPass")).thenReturn(false);
        assertThatThrownBy(() -> userService.updateCredentials(oldJson, newJson))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid current credentials");
    }

    @Test
    void deleteUser_Success() {
        LoginRequestDto credentials = new LoginRequestDto("mario@aruba.it", "password");
        User existingUser = new User();
        existingUser.setEmail("mario@aruba.it");
        existingUser.setPasswordHash("hashedPass");
        when(authRepository.findByEmail("mario@aruba.it")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("password", "hashedPass")).thenReturn(true);
        userService.deleteUser(credentials);
        verify(authRepository, times(1)).delete(existingUser);
    }

    @Test
    void deleteUser_UserNotFound_ThrowsException() {
        LoginRequestDto credentials = new LoginRequestDto("notfound@aruba.it", "pass");
        when(authRepository.findByEmail("notfound@aruba.it")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.deleteUser(credentials))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }
}
