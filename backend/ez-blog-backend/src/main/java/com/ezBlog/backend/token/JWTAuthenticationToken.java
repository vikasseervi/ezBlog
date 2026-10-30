package com.ezBlog.backend.token;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JWTAuthenticationToken extends AbstractAuthenticationToken {

    @Getter
    private final String token;
    @Getter
    private final TokenType type;
    private final Object principal;

    // Unauthenticated: "please validate this token"
    public JWTAuthenticationToken(String token, TokenType type) {
        super((Collection<? extends GrantedAuthority>) null);
        this.token = token;
        this.type = type;
        this.principal = null;
        setAuthenticated(false);
    }

    // Authenticated result
    public JWTAuthenticationToken(String token, TokenType type, Object principal,
                                  Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        this.type = type;
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override public Object getCredentials() { return token; }
    @Override public Object getPrincipal() { return principal; }
}