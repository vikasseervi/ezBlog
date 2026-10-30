package com.ezBlog.backend.security;

import com.ezBlog.backend.model.User;
import com.ezBlog.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BlogUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public BlogUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User ezBlogUser = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User.withUsername(ezBlogUser.getEmail())
                .password(ezBlogUser.getPassword() == null ? "" : ezBlogUser.getPassword())
                .roles(ezBlogUser.getRole().name())         // adds the ROLE_ prefix itself
                .disabled(!ezBlogUser.isEnabled())
                .build();
    }
}