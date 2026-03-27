package com.platformzeta.user.service;

import com.platformzeta.user.dto.LoginRequestDto;
import com.platformzeta.user.dto.LoginResponseDto;
import com.platformzeta.user.dto.RegisterRequestDto;
import com.platformzeta.user.dto.UserDto;
import com.platformzeta.user.entity.User;

public interface IUserService {

    LoginResponseDto loginUser(LoginRequestDto loginRequestDto);

    User registerUser(RegisterRequestDto registerRequestDto);

}
