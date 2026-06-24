package com.example.job_portal_backend.dtos.jobseeker;

import com.example.job_portal_backend.dtos.job.ResumeDto;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicantProfileDto {

    // USER INFO
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String profilePictureUrl;

    // JOB SEEKER PROFILE
    private Long profileId;
    private String headline;
    private String summary;
    private List<String> skills;
    private List<EducationDto> education;
    private List<ExperienceDto> experience;
    private List<CertificationDto> certifications;
    private List<ResumeDto> resumes;
    private List<String> preferredJobTypes;
    private List<String> preferredLocations;
}
