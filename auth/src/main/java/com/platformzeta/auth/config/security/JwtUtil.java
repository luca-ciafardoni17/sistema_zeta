package com.platformzeta.auth.config.security;

import com.platformzeta.auth.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    /**
     * JWT secret shared among microservices
     */
    private final Environment env;
    public static final String JWT_SECRET_KEY = "JWT_SECRET";
    public static final String JWT_SECRET_DEFAULT_VALUE = "jxgEQeXHuPq8VdbyYFNkANdudQ53YUn4";

    /**
     * @param authentication result from authentication function
     * @return a String representing the value of JWT Token, email and userId will be extracted by other microservices
     */
    public String generateJwtToken(Authentication authentication){
        String jwtToken;
        String secret = env.getProperty(JWT_SECRET_KEY, JWT_SECRET_DEFAULT_VALUE);
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        var fetchedUser = (User) authentication.getPrincipal();
        jwtToken = Jwts.builder()
                .issuer("Zeta platform")
                .subject("JWT Token")
                .claim("email", fetchedUser.getEmail())
                .claim("userId", fetchedUser.getId())
                .issuedAt(new java.util.Date())
                .expiration(new java.util.Date((new java.util.Date()).getTime() + 24 * 60 * 60 * 1000))
                .signWith(secretKey).compact();
        return jwtToken;
    }

}
