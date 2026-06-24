package com.example.job_portal_backend.service;

import com.example.job_portal_backend.dtos.analytics.JobSeekerDashboardResponse;
import com.example.job_portal_backend.dtos.jobseeker.*;
import com.example.job_portal_backend.entity.*;
import com.example.job_portal_backend.enums.UserRole;
import com.example.job_portal_backend.exceptions.ResourceNotFoundException;
import com.example.job_portal_backend.mappers.*;
import com.example.job_portal_backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobSeekerProfileService {

    private final JobSeekerProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final CertificationRepository certificationRepository;
    private final JobSeekerProfileMapper profileMapper;
    private final EducationMapper educationMapper;
    private final ExperienceMapper experienceMapper;
    private final CertificationMapper certificationMapper;
    private  final ResumeMapper resumeMapper;

    @Transactional
    public JobSeekerProfileDto getProfileByUserId(Long userId) {
        // First, fetch the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Try to find profile, or create new one if missing
        JobSeekerProfile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    JobSeekerProfile newProfile = JobSeekerProfile.builder()
                            .user(user)
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
                    return profileRepository.save(newProfile);
                });

        return profileMapper.toDto(profile);
    }


    @Transactional
    public JobSeekerProfile getOrCreateProfileEntity(Long userId) {

        return profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                    if (user.getRole() != UserRole.JOB_SEEKER) {
                        throw new IllegalStateException("User is not a JOB_SEEKER");
                    }

                    JobSeekerProfile profile = new JobSeekerProfile();
                    profile.setUser(user);

                    JobSeekerProfile saved = profileRepository.save(profile);

                    user.setJobSeekerProfile(saved);

                    return saved;
                });
    }




    public Page<JobSeekerProfileDto> searchProfiles(String keyword, Pageable pageable) {
        return profileRepository.searchProfiles(keyword, pageable)
                .map(profileMapper::toDto);
    }

    @Transactional
    public JobSeekerProfileDto createOrUpdateProfile(Long userId, JobSeekerProfileDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        JobSeekerProfile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> JobSeekerProfile.builder()
                        .user(user)
                        .build());

        /* ================= BASIC FIELDS ================= */
        profile.setHeadline(dto.getHeadline());
        profile.setSummary(dto.getSummary());
        profile.setSkills(dto.getSkills());
        profile.setPortfolioLinks(dto.getPortfolioLinks());
        profile.setPreferredJobTypes(dto.getPreferredJobTypes());
        profile.setPreferredLocations(dto.getPreferredLocations());

        // EDUCATION
        if (profile.getEducation() == null) {
            profile.setEducation(new ArrayList<>());
        } else {
            profile.getEducation().clear();
        }

        if (dto.getEducation() != null) {
            for (EducationDto eDto : dto.getEducation()) {
                Education edu = educationMapper.toEntity(eDto);
                edu.setJobSeekerProfile(profile);
                profile.getEducation().add(edu);
            }
        }

// EXPERIENCE
        if (profile.getExperience() == null) {
            profile.setExperience(new ArrayList<>());
        } else {
            profile.getExperience().clear();
        }

        if (dto.getExperience() != null) {
            for (ExperienceDto eDto : dto.getExperience()) {
                Experience exp = experienceMapper.toEntity(eDto);
                exp.setJobSeekerProfile(profile);
                profile.getExperience().add(exp);
            }
        }

// CERTIFICATIONS
        if (profile.getCertifications() == null) {
            profile.setCertifications(new ArrayList<>());
        } else {
            profile.getCertifications().clear();
        }

        if (dto.getCertifications() != null) {
            for (CertificationDto cDto : dto.getCertifications()) {
                Certification cert = certificationMapper.toEntity(cDto);
                cert.setJobSeekerProfile(profile);
                profile.getCertifications().add(cert);
            }
        }


        JobSeekerProfile saved = profileRepository.save(profile);
        return profileMapper.toDto(saved);
    }


    @Transactional
    public EducationDto addEducation(Long userId, EducationDto educationDto) {
        JobSeekerProfile profile = getOrCreateProfileEntity(userId);

        Education education = educationMapper.toEntity(educationDto);
        education.setJobSeekerProfile(profile);

        Education savedEducation = educationRepository.save(education);
        return educationMapper.toDto(savedEducation);
    }

    @Transactional
    public void removeEducation(Long educationId, Long userId) {
        JobSeekerProfile profile = getOrCreateProfileEntity(userId);

        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new ResourceNotFoundException("Education not found with id: " + educationId));


        if (!education.getJobSeekerProfile().getId().equals(profile.getId())) {
            throw new ResourceNotFoundException("Education not found for user");
        }

        educationRepository.delete(education);
    }

    @Transactional
    public ExperienceDto addExperience(Long userId, ExperienceDto experienceDto) {
        JobSeekerProfile profile = getOrCreateProfileEntity(userId);

        Experience experience = experienceMapper.toEntity(experienceDto);
        experience.setJobSeekerProfile(profile);

        Experience savedExperience = experienceRepository.save(experience);
        return experienceMapper.toDto(savedExperience);
    }

    @Transactional
    public void removeExperience(Long experienceId, Long userId) {
        JobSeekerProfile profile = getOrCreateProfileEntity(userId);

        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found with id: " + experienceId));

        // Verify experience belongs to user's profile
        if (!experience.getJobSeekerProfile().getId().equals(profile.getId())) {
            throw new ResourceNotFoundException("Experience not found for user");
        }

        experienceRepository.delete(experience);
    }

    @Transactional
    public CertificationDto addCertification(Long userId, CertificationDto certificationDto) {
        JobSeekerProfile profile = getOrCreateProfileEntity(userId);

        Certification certification = certificationMapper.toEntity(certificationDto);
        certification.setJobSeekerProfile(profile);

        Certification savedCertification = certificationRepository.save(certification);
        return certificationMapper.toDto(savedCertification);
    }

    @Transactional
    public void removeCertification(Long certificationId, Long userId) {
        JobSeekerProfile profile = getOrCreateProfileEntity(userId);

        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification not found with id: " + certificationId));

        // Verify certification belongs to user's profile
        if (!certification.getJobSeekerProfile().getId().equals(profile.getId())) {
            throw new ResourceNotFoundException("Certification not found for user");
        }

        certificationRepository.delete(certification);
    }


    public List<EducationDto> getEducations(Long userId) {
        JobSeekerProfile profile = getOrCreateProfileEntity(userId);

        return educationRepository.findByJobSeekerProfile(profile)
                .stream()
                .map(educationMapper::toDto)
                .toList();
    }

    public List<ExperienceDto> getExperiences(Long userId) {
        JobSeekerProfile profile = getOrCreateProfileEntity(userId);
        return experienceRepository.findByJobSeekerProfile(profile)
                .stream()
                .map(experienceMapper::toDto)
                .toList();
    }

    public List<CertificationDto> getCertifications(Long userId) {
        JobSeekerProfile profile = getOrCreateProfileEntity(userId);
        return certificationRepository.findByJobSeekerProfile(profile)
                .stream()
                .map(certificationMapper::toDto)
                .toList();
    }
    @Transactional
    public EducationDto updateEducation(Long userId, Long educationId, EducationDto dto) {
        JobSeekerProfile profile = getOrCreateProfileEntity(userId);

        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new ResourceNotFoundException("Education not found"));

        if (!education.getJobSeekerProfile().getId().equals(profile.getId())) {
            throw new ResourceNotFoundException("Unauthorized");
        }

        education.setDegree(dto.getDegree());
        education.setInstitution(dto.getInstitution());
        education.setStartDate(dto.getStartDate());
        education.setEndDate(dto.getEndDate());
        education.setGrade(dto.getGrade());

        return educationMapper.toDto(educationRepository.save(education));
    }

    @Transactional(readOnly = true)
    public JobSeekerDashboardResponse getDashboard(Long userId) {

        JobSeekerProfile profile = getOrCreateProfileEntity(userId);
        User user = profile.getUser();

        // 1️⃣ Total applications
        long totalApplications = profile.getApplications().size();

        // 2️⃣ Applications in last 30 days
        LocalDateTime last30Days = LocalDateTime.now().minusDays(30);

        long applicationsLast30Days = profile.getApplications().stream()
                .filter(app -> app.getAppliedAt() != null &&
                        app.getAppliedAt().isAfter(last30Days))
                .count();

        // 3️⃣ Status breakdown
        Map<String, Long> statusBreakdown =
                profile.getApplications().stream()
                        .collect(Collectors.groupingBy(
                                app -> app.getStatus().name(),
                                Collectors.counting()
                        ));

        // 4️⃣ Recent applications (last 5)
        List<JobSeekerDashboardResponse.RecentAppliedJob> recentApplications =
                profile.getApplications().stream()
                        .sorted(Comparator.comparing(JobApplication::getAppliedAt).reversed())
                        .limit(5)
                        .map(app -> new JobSeekerDashboardResponse.RecentAppliedJob(
                                app.getJob().getId(),
                                app.getJob().getTitle(),
                                app.getJob().getCompany().getName(),
                                app.getStatus(),
                                app.getAppliedAt()
                        ))
                        .toList();

        return JobSeekerDashboardResponse.builder()
                .jobSeekerId(profile.getId())
                .fullName(user.getFullName())
                .totalApplications(totalApplications)
                .applicationsLast30Days(applicationsLast30Days)
                .applicationStatusBreakdown(statusBreakdown)
                .recentApplications(recentApplications)
                .build();
    }

    @Transactional
    public ExperienceDto updateExperience(
            Long userId,
            Long experienceId,
            ExperienceDto dto) {

        JobSeekerProfile profile = getOrCreateProfileEntity(userId);

        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Experience not found with id: " + experienceId)
                );

        // Ownership check
        if (!experience.getJobSeekerProfile().getId().equals(profile.getId())) {
            throw new ResourceNotFoundException("Experience not found for user");
        }

        experience.setCompanyName(dto.getCompanyName());
        experience.setJobTitle(dto.getJobTitle());
        experience.setStartDate(dto.getStartDate());
        experience.setEndDate(dto.getEndDate());
        experience.setResponsibilities(dto.getResponsibilities());

        Experience updated = experienceRepository.save(experience);
        return experienceMapper.toDto(updated);
    }
    @Transactional
    public CertificationDto updateCertification(
            Long userId,
            Long certificationId,
            CertificationDto dto) {

        JobSeekerProfile profile = getOrCreateProfileEntity(userId);

        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Certification not found with id: " + certificationId)
                );

        // Ownership check
        if (!certification.getJobSeekerProfile().getId().equals(profile.getId())) {
            throw new ResourceNotFoundException("Certification not found for user");
        }

        certification.setTitle(dto.getTitle());
        certification.setIssuer(dto.getIssuer());
        certification.setIssueDate(dto.getIssueDate());
        certification.setExpiryDate(dto.getExpiryDate());
        certification.setCredentialUrl(dto.getCredentialUrl());

        Certification updated = certificationRepository.save(certification);
        return certificationMapper.toDto(updated);
    }

    public ApplicantProfileDto getApplicantProfile(Long profileId) {

        JobSeekerProfile profile = profileRepository
                .findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        User user = profile.getUser();

        return new ApplicantProfileDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getProfilePictureUrl(),

                profile.getId(),
                profile.getHeadline(),
                profile.getSummary(),
                profile.getSkills(),
                educationMapper.toDtoList(profile.getEducation()),
                experienceMapper.toDtoList(profile.getExperience()),
                certificationMapper.toDtoList(profile.getCertifications()),
                resumeMapper.toDtoList(profile.getResumes()),
                profile.getPreferredJobTypes(),
                profile.getPreferredLocations()
        );
    }
}
