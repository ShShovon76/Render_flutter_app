package com.example.job_portal_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "certifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private JobSeekerProfile jobSeekerProfile;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String issuer;

    @Column(nullable = false)
    private LocalDate issueDate;

    private LocalDate expiryDate;

    private String credentialUrl;
}
