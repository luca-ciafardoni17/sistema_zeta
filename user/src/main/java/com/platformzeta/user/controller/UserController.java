package com.platformzeta.user.controller;

import com.platformzeta.user.dto.LoginRequestDto;
import com.platformzeta.user.dto.LoginResponseDto;
import com.platformzeta.user.dto.UserDto;
import com.platformzeta.user.entity.User;
import com.platformzeta.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping( "/login")
    public ResponseEntity<LoginResponseDto> apiLogin(@RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = userService.loginUser(loginRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponseDto);
    }

    @PostMapping("/register")
    public ResponseEntity<String> apiRegister(@RequestBody UserDto userDto) {
        User user = userService.registerUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("User with email " + user.getEmail() + " created successfully!");
    }

}

