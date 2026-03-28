package com.platformzeta.auth.service.impl;

import com.platformzeta.auth.config.security.JwtUtil;
import com.platformzeta.auth.dto.LoginRequestDto;
import com.platformzeta.auth.dto.LoginResponseDto;
import com.platformzeta.auth.dto.RegisterRequestDto;
import com.platformzeta.auth.dto.UserDto;
import com.platformzeta.auth.entity.User;
import com.platformzeta.auth.repository.AuthRepository;
import com.platformzeta.auth.service.IAuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

/**
 * Javadoc for this class is defined in its implementation
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final PasswordEncoder passwordEncoder;
    private final AuthRepository authRepository;
    private final AuthenticationManager authenticationManager;
    // private final UserEventProducer userEventProducer;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponseDto loginUser(LoginRequestDto loginRequestDto) {
        try {
            var authResult = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequestDto.email(),
                    loginRequestDto.password()
            ));
            String jwtToken = jwtUtil.generateJwtToken(authResult);
            User loggedInUser = (User) authResult.getPrincipal();
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(loggedInUser, userDto);
            userDto.setId(loggedInUser.getId());
            userDto.setPassword(loggedInUser.getPasswordHash());
            return new LoginResponseDto(HttpStatus.OK.getReasonPhrase(), userDto, jwtToken);
        } catch (BadCredentialsException ex) {
            throw new RuntimeException("Invalid credentials");
        } catch (AuthenticationException ex) {
            throw new RuntimeException("Authentication failed");
        } catch (Exception ex) {
            throw new RuntimeException("An unexpected error occurred");
        }
    }

    @Override
    public User registerUser(RegisterRequestDto registerRequestDto) throws BadRequestException {
        if (!registerRequestDto.email().split("@")[1].equals("aruba.it")) {
            throw new BadRequestException("Email domain must be aruba.it");
        }
        Optional<User> existingUser = authRepository.findByEmail(registerRequestDto.email());
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.getEmail().equals(registerRequestDto.email())) {
                throw new RuntimeException("Email already registered!");
            }
        }
        User user = new User();
        BeanUtils.copyProperties(registerRequestDto, user);
        user.setPasswordHash(passwordEncoder.encode(registerRequestDto.password()));
        user.setCreatedBy("REGISTER FORM");
        User savedUser = authRepository.save(user);
        /*
        UserRegisteredEvent event = new UserRegisteredEvent(
                registerRequestDto.email(),
                registerRequestDto.accountHolder(),
                registerRequestDto.taxCode(),
                registerRequestDto.country(),
                registerRequestDto.province(),
                registerRequestDto.town(),
                registerRequestDto.address()
        );
        userEventProducer.publishUserRegistered(event);
        */
        return savedUser;
    }

    @Override
    @Transactional
    public LoginResponseDto updateCredentials(String oldCredentialsJson, String newCredentialsJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        LoginRequestDto oldCredentials = objectMapper.readValue(oldCredentialsJson, LoginRequestDto.class);
        LoginRequestDto newCredentials = objectMapper.readValue(newCredentialsJson, LoginRequestDto.class);
        User user = authRepository.findByEmail(oldCredentials.email()).orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(oldCredentials.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid current credentials");
        }
        if (!oldCredentials.email().equals(newCredentials.email()) &&
                authRepository.findByEmail(newCredentials.email()).isPresent()) {
            throw new RuntimeException("Email already in use!");
        }
        user.setEmail(newCredentials.email());
        user.setPasswordHash(passwordEncoder.encode(newCredentials.password()));
        authRepository.save(user);
        var authResult = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(newCredentials.email(), newCredentials.password())
        );
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        userDto.setPassword(user.getPasswordHash());
        userDto.setId(user.getId());
        String jwtToken = jwtUtil.generateJwtToken(authResult);
        /*
        UserEmailEvent event = new UserEmailEvent(
                updateRequestDto.email()
        );
        userEventProducer.publishUserUpdated(event);
        */
        return new LoginResponseDto(HttpStatus.OK.getReasonPhrase(), userDto, jwtToken);
    }

    @Override
    @Transactional
    public void deleteUser(LoginRequestDto credentials) {
        User user = authRepository.findByEmail(credentials.email()).orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(credentials.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid current credentials");
        }
        /*
        UserEmailEvent event = new UserEmailEvent(
                deleteRequestDto.email()
        );
        userEventProducer.publishUserDeleted(event);
        */
        authRepository.delete(user);
    }


}
