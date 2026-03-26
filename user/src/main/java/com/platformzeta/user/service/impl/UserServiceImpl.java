package com.platformzeta.user.service.impl;

import com.platformzeta.user.config.security.JwtUtil;
import com.platformzeta.user.dto.LoginRequestDto;
import com.platformzeta.user.dto.LoginResponseDto;
import com.platformzeta.user.dto.UserDto;
import com.platformzeta.user.entity.User;
import com.platformzeta.user.repository.UserRepository;
import com.platformzeta.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
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
    public User registerUser(UserDto userDto) {
        if (!userDto.getEmail().split("@")[1].equals("aruba.it")) {
            throw new RuntimeException("Email domain must be aruba.it");
        }
        Optional<User> existingUser = userRepository.findByEmailOrMobileNumber(userDto.getEmail(), userDto.getMobileNumber());
        if (existingUser.isPresent()) {
            Map<String, String> errors = new HashMap<>();
            User user = existingUser.get();
            if (user.getEmail().equals(userDto.getEmail())) {
                errors.put("email", "Email is already registered");
            }
            if (user.getMobileNumber().equals(userDto.getMobileNumber())) {
                errors.put("mobileNumber", "Mobile number is already registered");
            }
            throw new RuntimeException("Registration request refused:" + errors);
        }
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        user.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));
        user.setCreatedBy("REGISTER FORM");
        return userRepository.save(user);
    }

}
