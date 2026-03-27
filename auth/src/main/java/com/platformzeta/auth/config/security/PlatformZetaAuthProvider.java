package com.platformzeta.auth.config.security;

import com.platformzeta.auth.entity.User;
import com.platformzeta.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlatformZetaAuthProvider implements AuthenticationProvider {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * @param authentication parameter composed by email and hashed password
     * @return authentication result containing the requested user
     * @throws AuthenticationException
     */
    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String pwd = authentication.getCredentials().toString();
        User user = authRepository.findByEmail(email).orElseThrow(() -> new BadCredentialsException("Invalid credentials! (password)"));
        if (passwordEncoder.matches(pwd, user.getPasswordHash())) {
            return new UsernamePasswordAuthenticationToken(user, null, null);
        } else {
            throw new BadCredentialsException("Invalid credentials! (password)");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
