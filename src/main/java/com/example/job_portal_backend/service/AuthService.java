package com.example.job_portal_backend.service;

import com.example.job_portal_backend.config.TokenRefreshException;
import com.example.job_portal_backend.dtos.auth.*;
import com.example.job_portal_backend.entity.JobSeekerProfile;
import com.example.job_portal_backend.entity.RefreshToken;
import com.example.job_portal_backend.entity.User;
import com.example.job_portal_backend.enums.UserRole;
import com.example.job_portal_backend.exceptions.ResourceNotFoundException;
import com.example.job_portal_backend.exceptions.UserAlreadyExistsException;
import com.example.job_portal_backend.mappers.UserMapper;
import com.example.job_portal_backend.repository.JobSeekerProfileRepository;
import com.example.job_portal_backend.repository.RefreshTokenRepository;
import com.example.job_portal_backend.repository.UserRepository;
import com.example.job_portal_backend.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final CompanyService companyService;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;

    // ---------------------------------------------------
    // LOGIN
    // ---------------------------------------------------
    @Transactional
    public AuthResponse login(AuthRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // ✅ LOAD ENTITY FROM DB
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + request.getEmail()
                        )
                );

        String accessToken = jwtTokenUtil.generateAccessToken(user);
        RefreshToken refreshToken = createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .user(userMapper.toDto(user))
                .build();
    }

    // ---------------------------------------------------
    // REGISTER JOB SEEKER
    // ---------------------------------------------------
    @Transactional
    public AuthResponse registerJobSeeker(RegisterJobSeekerRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "Email already registered: " + request.getEmail()
            );
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.JOB_SEEKER)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        // ✅ CREATE PROFILE HERE
        JobSeekerProfile profile = JobSeekerProfile.builder()
                .user(savedUser)
                .headline("")
                .summary("")
                .skills(new ArrayList<>())
                .portfolioLinks(new ArrayList<>())
                .preferredJobTypes(new ArrayList<>())
                .preferredLocations(new ArrayList<>())
                .education(new ArrayList<>())
                .experience(new ArrayList<>())
                .certifications(new ArrayList<>())
                .resumes(new ArrayList<>())
                .applications(new ArrayList<>())
                .savedJobs(new ArrayList<>())
                .savedCompanies(new ArrayList<>())
                .build();

        jobSeekerProfileRepository.save(profile);

        // Optional: link back
        savedUser.setJobSeekerProfile(profile);

        // Security context
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        savedUser.getEmail(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + savedUser.getRole().name()))
                )
        );

        String accessToken = jwtTokenUtil.generateAccessToken(savedUser);
        RefreshToken refreshToken = createRefreshToken(savedUser);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .user(userMapper.toDto(savedUser))
                .build();
    }


    // ---------------------------------------------------
    // REGISTER EMPLOYER
    // ---------------------------------------------------
    @Transactional
    public AuthResponse registerEmployer(RegisterEmployerRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "Email already registered: " + request.getEmail()
            );
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.EMPLOYER)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        // Create company for employer
        companyService.createCompanyForEmployer(savedUser, request.getCompanyName());

        // ✅ Set Spring Security context manually
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        savedUser.getEmail(),
                        null,
                        Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_" + savedUser.getRole().name())
                        )
                )
        );

        String accessToken = jwtTokenUtil.generateAccessToken(savedUser);
        RefreshToken refreshToken = createRefreshToken(savedUser);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .user(userMapper.toDto(savedUser))
                .build();
    }

    // ---------------------------------------------------
    // REFRESH TOKEN
    // ---------------------------------------------------
    @Transactional
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .map(this::verifyRefreshToken)
                .orElseThrow(() ->
                        new TokenRefreshException(
                                request.getRefreshToken(),
                                "Refresh token not found"
                        )
                );

        User user = refreshToken.getUser();
        String accessToken = jwtTokenUtil.generateAccessToken(user);

        return RefreshTokenResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    // ---------------------------------------------------
    // LOGOUT
    // ---------------------------------------------------
    @Transactional
    public void logout(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + userId
                        )
                );

        refreshTokenRepository.revokeAllUserTokens(user);
        SecurityContextHolder.clearContext();

        log.info("User logged out: {}", user.getEmail());
    }

    // ---------------------------------------------------
    // PRIVATE HELPERS
    // ---------------------------------------------------
    private RefreshToken createRefreshToken(User user) {

        refreshTokenRepository.revokeAllUserTokens(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    private RefreshToken verifyRefreshToken(RefreshToken token) {

        if (token.isRevoked()) {
            throw new TokenRefreshException(
                    token.getToken(),
                    "Refresh token was revoked"
            );
        }

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new TokenRefreshException(
                    token.getToken(),
                    "Refresh token expired"
            );
        }

        return token;
    }

}
