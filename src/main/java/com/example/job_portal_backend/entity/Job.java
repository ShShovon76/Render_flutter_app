package com.example.job_portal_backend.entity;

import com.example.job_portal_backend.enums.ExperienceLevel;
import com.example.job_portal_backend.enums.JobStatus;
import com.example.job_portal_backend.enums.JobType;
import com.example.job_portal_backend.enums.SalaryType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private User employer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private String title;

    @Column(length = 4000, nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private JobCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExperienceLevel experienceLevel;

    private Double minSalary;
    private Double maxSalary;

    @Enumerated(EnumType.STRING)
    @Column(name = "salary_type", nullable = false)
    private SalaryType salaryType;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private boolean remoteAllowed = false;

    @ElementCollection
    @CollectionTable(name = "job_skills", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime postedAt;

    @Column(nullable = false)
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status = JobStatus.ACTIVE;

    @Column(nullable = false)
    private Integer viewsCount = 0;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<JobApplication> applications = new ArrayList<>();

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SavedJob> savedByJobSeekers = new ArrayList<>();

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<JobView> views = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        postedAt = LocalDateTime.now();
        if (status == null) status = JobStatus.ACTIVE;
    }

    public Integer getApplicantsCount() {
        return applications != null ? applications.size() : 0;
    }

    // Only use ID for equals/hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Job other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}