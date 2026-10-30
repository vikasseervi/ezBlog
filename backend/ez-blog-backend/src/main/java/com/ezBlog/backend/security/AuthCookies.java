package com.ezBlog.backend.security;

import org.springframework.http.ResponseCookie;

import java.time.Duration;

public final class AuthCookies {
    private AuthCookies() {}
    public static final String NAME = "RefreshToken";

    public static ResponseCookie create(String refreshToken, Duration maxAge) {
        return base(refreshToken).maxAge(maxAge).build();
    }

    public static ResponseCookie clear() {
        return base("").maxAge(Duration.ZERO).build();
    }

    private static ResponseCookie.ResponseCookieBuilder base(String value) {
        return ResponseCookie.from(NAME, value)
                .httpOnly(true)               // JavaScript can't read it (XSS-safe)
                .secure(true)                 // HTTPS only (Chrome also allows it on localhost)
                .sameSite("Strict")           // not sent on cross-site requests (CSRF-safe)
                .path(ApiPaths.REFRESH);      // browser sends it ONLY to the refresh endpoint
    }
}