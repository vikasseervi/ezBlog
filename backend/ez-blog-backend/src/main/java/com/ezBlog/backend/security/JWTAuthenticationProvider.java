package com.ezBlog.backend.security;

import com.ezBlog.backend.token.JWTAuthenticationToken;
import com.ezBlog.backend.util.JWTUtil;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public class JWTAuthenticationProvider implements AuthenticationProvider {

    private final JWTUtil jwtUtil;
    private final BlogUserDetailsService userDetailsService;

    public JWTAuthenticationProvider(JWTUtil jwtUtil, BlogUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JWTAuthenticationToken input = (JWTAuthenticationToken) authentication;

        String email = jwtUtil.validateAndGetSubject(input.getToken(), input.getType())
                .orElseThrow(() -> new BadCredentialsException("Invalid or expired token"));

        UserDetails user = userDetailsService.loadUserByUsername(email);   // fresh roles + enabled flag from DB
        if (!user.isEnabled()) {
            throw new DisabledException("Account disabled");
        }
        return new JWTAuthenticationToken(input.getToken(), input.getType(), user.getUsername(), user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JWTAuthenticationToken.class.isAssignableFrom(authentication);
    }
}