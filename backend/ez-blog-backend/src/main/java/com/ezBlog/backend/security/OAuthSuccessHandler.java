package com.ezBlog.backend.security;

import com.ezBlog.backend.model.User;
import com.ezBlog.backend.service.RegistrationService;
import com.ezBlog.backend.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final RegistrationService registrationService;
    private final JWTUtil jwtUtil;
    private final String frontendUrl;

    public OAuthSuccessHandler(RegistrationService registrationService,
                               JWTUtil jwtUtil,
                               @Value("${app.frontend-url}") String frontendUrl) {
        this.registrationService = registrationService;
        this.jwtUtil = jwtUtil;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User principal = ((OAuth2AuthenticationToken) authentication).getPrincipal();

        String email = principal.getAttribute("email");
        Boolean verified = principal.getAttribute("email_verified");
        if (email == null || !Boolean.TRUE.equals(verified)) {
            response.sendRedirect(frontendUrl + "/login?error=email_not_verified");
            return;
        }

        // Finds the existing account (password or Google) or creates a Google-only one,
        // and discards any stale pending registration for this email.
        User user = registrationService.findOrCreateGoogleUser(
                email.trim().toLowerCase(Locale.ROOT), principal.getAttribute("name"));

        if (!user.isEnabled()) {
            response.sendRedirect(frontendUrl + "/login?error=account_disabled");
            return;
        }

        String refresh = jwtUtil.generateRefreshToken(user.getEmail());
        response.addHeader(HttpHeaders.SET_COOKIE, AuthCookies.create(refresh, jwtUtil.getRefreshTtl()).toString());
        response.sendRedirect(frontendUrl + "/oauth/callback");   // React page then calls POST .../auth/refresh
    }
}