package com.ezBlog.backend.repository;

import com.ezBlog.backend.model.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findByTokenHash(String tokenHash);

    @Modifying
    @Query("delete from EmailVerification v where v.email = :email")
    void deleteByEmail(@Param("email") String email);        // call inside a @Transactional method
}