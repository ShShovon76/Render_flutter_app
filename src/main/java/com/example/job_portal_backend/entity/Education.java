package com.example.job_portal_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "educations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private JobSeekerProfile jobSeekerProfile;

    @Column(nullable = false)
    private String degree;

    @Column(nullable = false)
    private String institution;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    private String grade;
}
