package com.platformzeta.auth.service;

import com.platformzeta.auth.dto.LoginRequestDto;
import com.platformzeta.auth.dto.LoginResponseDto;
import com.platformzeta.auth.dto.RegisterRequestDto;
import com.platformzeta.auth.entity.User;

public interface IAuthService {

    /**
     * @param loginRequestDto - auth infos
     * @return - logged user with jwt token
     */
    LoginResponseDto loginUser(LoginRequestDto loginRequestDto);

    /**
     * @param registerRequestDto - auth and anagraphic infos
     * @return - newly created user data
     */
    User registerUser(RegisterRequestDto registerRequestDto);

    /**
     * @param oldCredentials - auth infos to change
     * @param newCredentials - new auth infos
     * @return - logged user with jwt token
     */
    LoginResponseDto updateCredentials(String oldCredentials, String newCredentials);

    /**
     * @param credentials - auth infos of user to delete
     */
    void deleteUser(LoginRequestDto credentials);
}
