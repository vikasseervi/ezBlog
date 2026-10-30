package com.ezBlog.backend.service;

import com.ezBlog.backend.model.ProfileResponse;
import com.ezBlog.backend.model.UpdateProfileRequest;
import com.ezBlog.backend.model.User;
import com.ezBlog.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProfileService {

    private final UserRepository userRepository;

    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(String email) {
        return ProfileResponse.from(findUser(email));
    }

    @Transactional
    public ProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = findUser(email);

        String name = request.name().trim();
        if (name.length() < 2) {   // "  a " passes the annotation but is too short once trimmed
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name must be between 2 and 100 characters");
        }

        user.setName(name);
        user.setBio(request.bio() == null ? "" : request.bio().trim());
        user.setLocation(blankToNull(request.location()));
        user.setWebsite(blankToNull(request.website()));
        return ProfileResponse.from(userRepository.save(user));   // @PreUpdate refreshes updatedAt
    }

    private User findUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated"));
    }

    private static String blankToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}