package com.ezBlog.backend.model;

import java.time.Instant;

public record ProfileResponse(Long id, String name, String email, String bio,
                              String location, String website, String role, Instant createdAt) {

    public static ProfileResponse from(User user) {
        return new ProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getBio(),
                user.getLocation(), user.getWebsite(), user.getRole().name(), user.getCreatedAt());
    }
}