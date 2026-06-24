package com.example.job_portal_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_seeker_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobSeekerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;


    private String headline;

    @Column(length = 2000)
    private String summary;

    @ElementCollection
    @CollectionTable(name = "job_seeker_skills", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();

    @OneToMany(mappedBy = "jobSeekerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> education = new ArrayList<>();

    @OneToMany(mappedBy = "jobSeekerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experience> experience = new ArrayList<>();

    @OneToMany(mappedBy = "jobSeekerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certification> certifications = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "job_seeker_portfolio_links", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "portfolio_link")
    private List<String> portfolioLinks = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "job_seeker_preferred_job_types", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "preferred_job_type")
    private List<String> preferredJobTypes = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "job_seeker_preferred_locations", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "preferred_location")
    private List<String> preferredLocations = new ArrayList<>();


    @OneToMany(mappedBy = "jobSeeker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resume> resumes = new ArrayList<>();

    @OneToMany(mappedBy = "jobSeeker", cascade = CascadeType.ALL)
    private List<JobApplication> applications = new ArrayList<>();

    @OneToMany(mappedBy = "jobSeeker", cascade = CascadeType.ALL)
    private List<SavedJob> savedJobs = new ArrayList<>();

    @OneToMany(mappedBy = "jobSeeker", cascade = CascadeType.ALL)
    private List<SavedCompany> savedCompanies = new ArrayList<>();
}

