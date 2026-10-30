package com.ezBlog.backend.security;

import com.ezBlog.backend.token.JWTAuthenticationToken;
import com.ezBlog.backend.token.TokenType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTValidationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;

    public JWTValidationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            try {
                Authentication result = authenticationManager.authenticate(
                        new JWTAuthenticationToken(header.substring(7).trim(), TokenType.ACCESS));
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(result);
                SecurityContextHolder.setContext(context);
            } catch (AuthenticationException ex) {
                // Do NOT throw. Continue as anonymous: protected URLs get a clean 401 from the
                // entry point, public URLs (e.g. GET posts) still work with a stale token.
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }
}