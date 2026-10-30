package com.ezBlog.backend.service;

import com.ezBlog.backend.model.EmailVerification;
import com.ezBlog.backend.model.User;
import com.ezBlog.backend.repository.EmailVerificationRepository;
import com.ezBlog.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Optional;

@Service
public class RegistrationService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final EmailVerificationRepository verificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final Duration ttl;

    public RegistrationService(UserRepository userRepository,
                               EmailVerificationRepository verificationRepository,
                               PasswordEncoder passwordEncoder,
                               MailService mailService,
                               @Value("${app.verification.ttl-minutes:60}") long ttlMinutes) {
        this.userRepository = userRepository;
        this.verificationRepository = verificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.ttl = Duration.ofMinutes(ttlMinutes);
    }

    /** Step 1: store a pending request and email the link. Never touches `users`. */
    @Transactional
    public void register(String name, String rawEmail, String rawPassword) {
        String email = rawEmail.trim().toLowerCase(Locale.ROOT);
        Optional<User> existing = userRepository.findByEmailIgnoreCase(email);

        if (existing.isPresent() && existing.get().getPassword() != null) {
            mailService.sendAccountExists(email);     // nothing to verify or change
            return;
        }

        verificationRepository.deleteByEmail(email);  // a newer request replaces the old one (old link dies)

        String token = newToken();
        EmailVerification pending = new EmailVerification();
        pending.setEmail(email);
        pending.setName(name.trim());
        pending.setPasswordHash(passwordEncoder.encode(rawPassword));
        pending.setTokenHash(sha256(token));
        pending.setCreatedAt(Instant.now());
        pending.setExpiresAt(Instant.now().plus(ttl));
        verificationRepository.save(pending);

        // existing.isPresent() here means: a Google-only account that wants to add a password (merge)
        mailService.sendVerification(email, token, existing.isPresent());
    }

    /** Step 2: the link was clicked. Create the account, or add the password to the existing one. */
    @Transactional
    public void verify(String token) {
        EmailVerification pending = verificationRepository.findByTokenHash(sha256(token))
                .filter(p -> p.getExpiresAt().isAfter(Instant.now()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired link"));

        User user = userRepository.findByEmailIgnoreCase(pending.getEmail()).orElseGet(User::new);
        if (user.getPassword() != null) {   // got a password in the meantime (e.g. a second link was used first)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired link");
        }

        if (user.getId() == null) {         // brand-new account: take name and email from the request
            user.setEmail(pending.getEmail());
            user.setName(pending.getName());
        }                                   // merge: keep the existing (Google) name
        user.setPassword(pending.getPasswordHash());
        userRepository.save(user);

        verificationRepository.delete(pending);   // single use
    }

    /** Called by the Google success handler. Google proved the mailbox, so this account is trustworthy. */
    @Transactional
    public User findOrCreateGoogleUser(String email, String googleName) {
        verificationRepository.deleteByEmail(email);   // any outstanding registration for this email is now stale
        return userRepository.findByEmailIgnoreCase(email).orElseGet(() -> {
            User user = new User();
            user.setEmail(email);
            String name = (googleName == null ? "" : googleName.trim());
            if (name.length() < 2) name = email.substring(0, email.indexOf('@'));
            user.setName(name.length() > 100 ? name.substring(0, 100) : name);
            return userRepository.save(user);
        });
    }

    private static String newToken() {
        byte[] bytes = new byte[32];                  // 256 bits from the OS secure random generator
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);   // URL-safe, no encoding needed
    }

    private static String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);  // 64 hex chars
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}