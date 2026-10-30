package com.ezBlog.backend.controller;

import com.ezBlog.backend.security.ApiPaths;
import com.ezBlog.backend.security.AuthCookies;
import com.ezBlog.backend.service.RegistrationService;
import com.ezBlog.backend.token.JWTAuthenticationToken;
import com.ezBlog.backend.token.TokenType;
import com.ezBlog.backend.util.JWTUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping(ApiPaths.AUTH)
public class AuthController {

    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}

    public record RegisterRequest(
            @NotBlank @Size(min = 2, max = 100) String name,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8, max = 72) String password) {}   // BCrypt only uses the first 72 bytes

    public record VerifyEmailRequest(@NotBlank String token) {}

    public record TokenResponse(String accessToken, String tokenType, long expiresIn) {}

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RegistrationService registrationService;

    public AuthController(AuthenticationManager authenticationManager,
                          JWTUtil jwtUtil,
                          RegistrationService registrationService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.registrationService = registrationService;
    }

    // Step 1 of registration: stores a PENDING request and emails a link. Never creates the user.
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, String> register(@Valid @RequestBody RegisterRequest req) {
        registrationService.register(req.name(), req.email(), req.password());
        return Map.of("message", "If this email can be registered, we've sent you a verification link.");
    }

    // Step 2: the React /verify-email page POSTs the token from the link.
    @PostMapping("/verify-email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verifyEmail(@Valid @RequestBody VerifyEmailRequest req) {
        registrationService.verify(req.token());
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email().trim().toLowerCase(Locale.ROOT), req.password()));
        return tokens(auth.getName());
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @CookieValue(name = AuthCookies.NAME, required = false) String refreshToken) {
        if (refreshToken == null) {
            throw new BadCredentialsException("Missing refresh token");
        }
        Authentication auth = authenticationManager.authenticate(
                new JWTAuthenticationToken(refreshToken, TokenType.REFRESH));
        return tokens((String) auth.getPrincipal());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, AuthCookies.clear().toString())
                .build();
    }

    private ResponseEntity<TokenResponse> tokens(String email) {
        String access = jwtUtil.generateAccessToken(email);
        String refresh = jwtUtil.generateRefreshToken(email);           // rotated on every refresh
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, AuthCookies.create(refresh, jwtUtil.getRefreshTtl()).toString())
                .body(new TokenResponse(access, "Bearer", jwtUtil.getAccessTtl().toSeconds()));
    }

    // Wrong password, unknown email, bad/expired refresh token, disabled account: all look identical to the caller
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.SET_COOKIE, AuthCookies.clear().toString())
                .body(Map.of("error", "Invalid credentials"));
    }
}