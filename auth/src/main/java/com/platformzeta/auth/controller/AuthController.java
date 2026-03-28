package com.platformzeta.auth.controller;

import com.platformzeta.auth.dto.ErrorResponseDto;
import com.platformzeta.auth.dto.LoginRequestDto;
import com.platformzeta.auth.dto.LoginResponseDto;
import com.platformzeta.auth.dto.RegisterRequestDto;
import com.platformzeta.auth.entity.User;
import com.platformzeta.auth.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Auth Controller",
        description = "Auth Controller, its purpose is define and provision auth user and jwt related token"
)
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @Operation(
            summary = "POST (to get) Login",
            description = "API for getting JWT Token with correct login credentials"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "HTTP Status Unauthorized",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping( "/login")
    public ResponseEntity<LoginResponseDto> apiLogin(@RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = authService.loginUser(loginRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponseDto);
    }

    @Operation(
            summary = "POST Register",
            description = "API for register a user in Auth database"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "HTTP Status Created"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "HTTP Status Unauthorized",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<String> apiRegister(@RequestBody RegisterRequestDto registerRequestDto) throws BadRequestException {
        User user = authService.registerUser(registerRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("User with email " + user.getEmail() + " created successfully!");
    }

    @Operation(
            summary = "PUT Update user credentials",
            description = "API for updating a user credentials in Auth database"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "HTTP Status Unauthorized",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PutMapping("")
    public ResponseEntity<LoginResponseDto> updateCredentials(
            @RequestPart(value = "oldCredentials") String oldCredentialsJson,
            @RequestPart(value = "newCredentials") String newCredentialsJson
    ) {
        LoginResponseDto newCredLoginResponseDto = authService.updateCredentials(oldCredentialsJson, newCredentialsJson);
        return ResponseEntity.status(HttpStatus.OK).body(newCredLoginResponseDto);
    }

    @Operation(
            summary = "DELETE user",
            description = "API for deleting a user credentials in Auth database"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "HTTP Status Unauthorized",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @DeleteMapping("")
    public ResponseEntity<String> deleteUser(@RequestBody LoginRequestDto credentials) {
        authService.deleteUser(credentials);
        return ResponseEntity.status(HttpStatus.OK).body("User " + credentials.email() + " deleted successfully");
    }

}

