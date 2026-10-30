package com.ezBlog.backend.controller;

import com.ezBlog.backend.model.ProfileResponse;
import com.ezBlog.backend.model.UpdateProfileRequest;
import com.ezBlog.backend.security.ApiPaths;
import com.ezBlog.backend.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.BASE + "/users")
public class UserController {

    private final ProfileService profileService;

    public UserController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public ProfileResponse me(Authentication authentication) {
        return profileService.getProfile(authentication.getName());
    }

    @PutMapping("/me")
    public ProfileResponse updateMe(@Valid @RequestBody UpdateProfileRequest request,
                                    Authentication authentication) {
        return profileService.updateProfile(authentication.getName(), request);
    }
}