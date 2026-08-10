package com.ezBlog.backend.controller;


import com.ezBlog.backend.model.LoginRequest;
import com.ezBlog.backend.model.User;
import com.ezBlog.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));

            if (!user.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.status(401).body("Invalid credentials");
            }

            User userProfile = user.toPublicProfile();

            Map<String, Object> response = new HashMap<>();
            response.put("token", "dummy-token");
            response.put("type", "Bearer");
            response.put("user", userProfile);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader(value = "x-user-id", required = false) Long userId) {
        try {
            if (userId == null) {
                return ResponseEntity.status(401).body("Not authenticated");
            }

            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(user.toPublicProfile());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error");
        }
    }
}

