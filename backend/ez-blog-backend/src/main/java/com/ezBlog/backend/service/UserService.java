package com.ezBlog.backend.service;

import com.ezBlog.backend.model.User;
import com.ezBlog.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    public User save(User user) {
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(Instant.from(LocalDateTime.now()));
        }
        user.setUpdatedAt(Instant.from(LocalDateTime.now()));

        return userRepository.save(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }
}
