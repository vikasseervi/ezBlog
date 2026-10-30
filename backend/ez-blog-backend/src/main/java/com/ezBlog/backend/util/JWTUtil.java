package com.ezBlog.backend.util;

import com.ezBlog.backend.token.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
public class JWTUtil {

    private static final String CLAIM_TYPE = "type";

    private final SecretKey key;
    private final String issuer;
    @Getter
    private final Duration accessTtl;
    @Getter
    private final Duration refreshTtl;

    public JWTUtil(@Value("${app.jwt.secret}") String base64Secret,
                   @Value("${app.jwt.issuer:ezblog}") String issuer,
                   @Value("${app.jwt.access-minutes:15}") long accessMinutes,
                   @Value("${app.jwt.refresh-days:7}") long refreshDays) {
        byte[] bytes = Decoders.BASE64.decode(base64Secret);
        if (bytes.length < 32) {
            throw new IllegalStateException("app.jwt.secret must decode to at least 32 bytes");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.issuer = issuer;
        this.accessTtl = Duration.ofMinutes(accessMinutes);
        this.refreshTtl = Duration.ofDays(refreshDays);
    }

    public String generateAccessToken(String email)  { return build(email, TokenType.ACCESS, accessTtl); }
    public String generateRefreshToken(String email) { return build(email, TokenType.REFRESH, refreshTtl); }

    /** Returns the email if the token is genuine, unexpired, from us, AND of the expected type. */
    public Optional<String> validateAndGetSubject(String token, TokenType expected) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(issuer)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            if (!expected.name().equals(claims.get(CLAIM_TYPE, String.class))) {
                return Optional.empty();
            }
            return Optional.ofNullable(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();   // bad signature, expired, malformed, null...
        }
    }

    private String build(String email, TokenType type, Duration ttl) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .issuer(issuer)
                .id(UUID.randomUUID().toString())
                .claim(CLAIM_TYPE, type.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(ttl)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }
}