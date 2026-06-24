package com.example.job_portal_backend.dtos.jobseeker;

import com.example.job_portal_backend.dtos.company.SavedCompanyDto;
import com.example.job_portal_backend.dtos.job.JobApplicationDto;
import com.example.job_portal_backend.dtos.job.ResumeDto;
import com.example.job_portal_backend.dtos.other.SavedJobDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSeekerProfileDto {

    private Long id;

    private Long userId;

    private String headline;

    private String summary;

    private List<String> skills;
    private List<EducationDto> education;
    private List<ExperienceDto> experience;
    private List<CertificationDto> certifications;

    private List<String> portfolioLinks;

    private List<String> preferredJobTypes;
    private List<String> preferredLocations;

    // NEW — resumes list
    private List<ResumeDto> resumes;
    private List<JobApplicationDto> applications;
    private List<SavedJobDto> savedJobs;
    private List<SavedCompanyDto> savedCompanies;
}

