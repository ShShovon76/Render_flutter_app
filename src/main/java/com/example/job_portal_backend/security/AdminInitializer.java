package com.example.job_portal_backend.security;

import com.example.job_portal_backend.entity.User;
import com.example.job_portal_backend.enums.UserRole;
import com.example.job_portal_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {

        String adminEmail = "admin@example.com";

        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin already exists");
            return;
        }

        User admin = User.builder()
                .fullName("Admin")
                .email(adminEmail)
                .password(passwordEncoder.encode("demo123"))
                .role(UserRole.ADMIN)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(admin);

        log.info("✅ Default admin created: {}", adminEmail);
    }
}
