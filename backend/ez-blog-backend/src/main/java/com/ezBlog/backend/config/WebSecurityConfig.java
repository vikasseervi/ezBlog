package com.ezBlog.backend.config;

import com.ezBlog.backend.security.*;
import com.ezBlog.backend.util.JWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private final JWTUtil jwtUtil;
    private final BlogUserDetailsService userDetailsService;
    private final List<String> allowedOrigins;
    private final String frontendUrl;

    public WebSecurityConfig(JWTUtil jwtUtil,
                             BlogUserDetailsService userDetailsService,
                             @Value("${app.cors.allowed-origins}") List<String> allowedOrigins,
                             @Value("${app.frontend-url}") String frontendUrl) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.allowedOrigins = allowedOrigins;
        this.frontendUrl = frontendUrl;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean
    public JWTAuthenticationProvider jwtAuthenticationProvider() {
        return new JWTAuthenticationProvider(jwtUtil, userDetailsService);
    }

    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider dao,
                                                       JWTAuthenticationProvider jwt) {
        return new ProviderManager(dao, jwt);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);                 // exact origins only: required when credentials are on
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setAllowCredentials(true);                         // the refresh cookie must travel cross-origin
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(ApiPaths.BASE + "/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationManager authenticationManager,
                                                   OAuthSuccessHandler oAuthSuccessHandler,
                                                   CorsConfigurationSource corsConfigurationSource) throws Exception {

        JWTValidationFilter jwtValidationFilter = new JWTValidationFilter(authenticationManager);

        http
                .csrf(AbstractHttpConfigurer::disable)   // API uses a Bearer header; the refresh cookie is SameSite=Strict and only reaches /auth/refresh
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, ApiPaths.BASE, ApiPaths.BASE + "/").permitAll()
                        .requestMatchers(ApiPaths.AUTH + "/**").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**", "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, ApiPaths.BASE + "/posts/my-posts").authenticated()
                        .requestMatchers(HttpMethod.GET, ApiPaths.BASE + "/posts/**").permitAll()
                        .requestMatchers(ApiPaths.BASE + "/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth -> oauth
                        .successHandler(oAuthSuccessHandler)
                        .failureHandler((req, res, ex) -> res.sendRedirect(frontendUrl + "/login?error=oauth_failed"))
                )

                .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

                .headers(h -> h
                        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'none'; frame-ancestors 'none'"))
                        .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31_536_000))
                        .referrerPolicy(r -> r.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
                )

                .addFilterBefore(jwtValidationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}