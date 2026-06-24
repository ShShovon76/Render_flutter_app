package com.example.job_portal_backend.controller;

import com.example.job_portal_backend.dtos.auth.*;
import com.example.job_portal_backend.entity.User;
import com.example.job_portal_backend.exceptions.ResourceNotFoundException;
import com.example.job_portal_backend.repository.UserRepository;
import com.example.job_portal_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register/job-seeker")
    public ResponseEntity<AuthResponse> registerJobSeeker(@Valid @RequestBody RegisterJobSeekerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerJobSeeker(request));
    }

    @PostMapping("/register/employer")
    public ResponseEntity<AuthResponse> registerEmployer(@Valid @RequestBody RegisterEmployerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerEmployer(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails userDetails) {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            authService.logout(user.getId());
        }
        return ResponseEntity.ok().build();
    }
}
