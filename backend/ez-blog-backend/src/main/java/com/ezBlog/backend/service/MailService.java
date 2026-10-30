package com.ezBlog.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final String from;
    private final String frontendUrl;
    private final long ttlMinutes;

    public MailService(JavaMailSender mailSender,
                       @Value("${app.mail.from}") String from,
                       @Value("${app.frontend-url}") String frontendUrl,
                       @Value("${app.verification.ttl-minutes:60}") long ttlMinutes) {
        this.mailSender = mailSender;
        this.from = from;
        this.frontendUrl = frontendUrl;
        this.ttlMinutes = ttlMinutes;
    }

    @Async   // the HTTP response doesn't wait for SMTP, so response time doesn't reveal whether the email exists
    public void sendVerification(String to, String token, boolean addingPasswordToExistingAccount) {
        String link = frontendUrl + "/verify-email?token=" + token;
        if (addingPasswordToExistingAccount) {
            send(to, "Confirm adding a password to your ezBlog account",
                    "Someone asked to add a password to your existing ezBlog account (the one you use with Google).\n\n"
                            + "Only if this was YOU, confirm within " + ttlMinutes + " minutes:\n" + link + "\n\n"
                            + "If it wasn't you, ignore this email. Nothing will change.");
        } else {
            send(to, "Verify your email for ezBlog",
                    "Welcome to ezBlog! Confirm your email within " + ttlMinutes + " minutes to activate your account:\n"
                            + link + "\n\nIf you didn't sign up, ignore this email.");
        }
    }

    @Async
    public void sendAccountExists(String to) {
        send(to, "You already have an ezBlog account",
                "Someone tried to register with this email, but an account already exists.\n"
                        + "If that was you, just log in. If it wasn't, you can ignore this email.");
    }

    private void send(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (MailException e) {
            log.error("Failed to send '{}' email", subject, e);   // runs after the response is gone; just log it
        }
    }
}