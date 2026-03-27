package com.platformzeta.user.service.impl;

import com.platformzeta.user.config.security.JwtUtil;
import com.platformzeta.user.dto.LoginRequestDto;
import com.platformzeta.user.dto.LoginResponseDto;
import com.platformzeta.user.dto.RegisterRequestDto;
import com.platformzeta.user.dto.UserDto;
import com.platformzeta.user.entity.User;
import com.platformzeta.user.kafka.UserEventProducer;
import com.platformzeta.user.kafka.event.UserRegisteredEvent;
import com.platformzeta.user.repository.UserRepository;
import com.platformzeta.user.service.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
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
    public User registerUser(RegisterRequestDto registerRequestDto) {
        if (!registerRequestDto.email().split("@")[1].equals("aruba.it")) {
            throw new RuntimeException("Email domain must be aruba.it");
        }
        Optional<User> existingUser = userRepository.findByEmail(registerRequestDto.email());
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
        User savedUser = userRepository.save(user);
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
        User user = userRepository.findByEmail(oldCredentials.email()).orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(oldCredentials.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid current credentials");
        }
        if (!oldCredentials.email().equals(newCredentials.email()) &&
                userRepository.findByEmail(newCredentials.email()).isPresent()) {
            throw new RuntimeException("Email already in use!");
        }
        user.setEmail(newCredentials.email());
        user.setPasswordHash(passwordEncoder.encode(newCredentials.password()));
        userRepository.save(user);
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
        User user = userRepository.findByEmail(credentials.email()).orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(credentials.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid current credentials");
        }
        /*
        UserEmailEvent event = new UserEmailEvent(
                deleteRequestDto.email()
        );
        userEventProducer.publishUserDeleted(event);
        */
        userRepository.delete(user);
    }


}
