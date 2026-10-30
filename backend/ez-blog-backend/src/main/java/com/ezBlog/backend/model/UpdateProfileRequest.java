package com.ezBlog.backend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @Size(max = 500, message = "Bio must be at most 500 characters")
        String bio,

        @Size(max = 100, message = "Location must be at most 100 characters")
        String location,

        // Only http(s) links. A "javascript:" URL in an <a href> would be an XSS hole.
        @Size(max = 200, message = "Website must be at most 200 characters")
        @Pattern(regexp = "^$|^https?://\\S+$", message = "Website must start with http:// or https://")
        String website) {}