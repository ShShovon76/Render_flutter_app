package com.example.job_portal_backend.controller;

import com.example.job_portal_backend.dtos.analytics.JobSeekerDashboardResponse;
import com.example.job_portal_backend.dtos.jobseeker.*;
import com.example.job_portal_backend.security.UserPrincipal;
import com.example.job_portal_backend.service.FileStorageService;
import com.example.job_portal_backend.service.JobSeekerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/job-seekers")
@RequiredArgsConstructor
public class JobSeekerProfileController {

    private final JobSeekerProfileService profileService;
    private final FileStorageService fileStorageService;
    @GetMapping("/profile/{userId}")
    public ResponseEntity<JobSeekerProfileDto> getProfileByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getProfileByUserId(userId));
    }

    @GetMapping("/{userId}/dashboard")
    public ResponseEntity<JobSeekerDashboardResponse> getDashboard(
            @PathVariable Long userId) {

        return ResponseEntity.ok(profileService.getDashboard(userId));
    }

    @GetMapping("/applicant/{profileId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApplicantProfileDto> getApplicantProfile(
            @PathVariable Long profileId) {
        return ResponseEntity.ok(profileService.getApplicantProfile(profileId));
    }


    @GetMapping("/search")
    public ResponseEntity<Page<JobSeekerProfileDto>> searchProfiles(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(profileService.searchProfiles(keyword, pageable));
    }

    @PostMapping("/profile/{userId}")
    public ResponseEntity<JobSeekerProfileDto> createOrUpdateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody JobSeekerProfileDto profileDto) {
        return ResponseEntity.ok(profileService.createOrUpdateProfile(userId, profileDto));
    }

    @GetMapping("/{userId}/education")
    public ResponseEntity<List<EducationDto>> getEducations(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getEducations(userId));
    }

    @PostMapping("/{userId}/education")
    public ResponseEntity<EducationDto> addEducation(
            @PathVariable Long userId,
            @Valid @RequestBody EducationDto educationDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(profileService.addEducation(userId, educationDto));
    }

    @PutMapping("/{userId}/education/{educationId}")
    public ResponseEntity<EducationDto> updateEducation(
            @PathVariable Long userId,
            @PathVariable Long educationId,
            @Valid @RequestBody EducationDto dto) {

        return ResponseEntity.ok(
                profileService.updateEducation(userId, educationId, dto)
        );
    }

    @DeleteMapping("/{userId}/education/{educationId}")
    public ResponseEntity<Void> removeEducation(
            @PathVariable Long userId,
            @PathVariable Long educationId) {
        profileService.removeEducation(educationId, userId);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{userId}/experience/{experienceId}")
    public ResponseEntity<ExperienceDto> updateExperience(
            @PathVariable Long userId,
            @PathVariable Long experienceId,
            @Valid @RequestBody ExperienceDto dto) {

        return ResponseEntity.ok(
                profileService.updateExperience(userId, experienceId, dto)
        );
    }

    @GetMapping("/{userId}/experience")
    public ResponseEntity<List<ExperienceDto>> getExperiences(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getExperiences(userId));
    }

    @PostMapping("/{userId}/experience")
    public ResponseEntity<ExperienceDto> addExperience(
            @PathVariable Long userId,
            @Valid @RequestBody ExperienceDto experienceDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(profileService.addExperience(userId, experienceDto));
    }

    @DeleteMapping("/{userId}/experience/{experienceId}")
    public ResponseEntity<Void> removeExperience(
            @PathVariable Long userId,
            @PathVariable Long experienceId) {
        profileService.removeExperience(experienceId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/certifications")
    public ResponseEntity<List<CertificationDto>> getCertifications(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getCertifications(userId));
    }

    @PostMapping("/{userId}/certifications")
    public ResponseEntity<CertificationDto> addCertification(
            @PathVariable Long userId,
            @Valid @RequestBody CertificationDto certificationDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(profileService.addCertification(userId, certificationDto));
    }

    @PutMapping("/{userId}/certifications/{certificationId}")
    public ResponseEntity<CertificationDto> updateCertification(
            @PathVariable Long userId,
            @PathVariable Long certificationId,
            @Valid @RequestBody CertificationDto dto) {

        return ResponseEntity.ok(
                profileService.updateCertification(userId, certificationId, dto)
        );
    }

    @DeleteMapping("/{userId}/certifications/{certificationId}")
    public ResponseEntity<Void> removeCertification(
            @PathVariable Long userId,
            @PathVariable Long certificationId) {
        profileService.removeCertification(certificationId, userId);
        return ResponseEntity.noContent().build();
    }

}
