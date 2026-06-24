package com.example.job_portal_backend.mappers;

import com.example.job_portal_backend.dtos.job.*;
import com.example.job_portal_backend.entity.Job;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobMapper {

    public JobResponseDto toResponseDto(Job job) {
        if (job == null) {
            return null;
        }

        return JobResponseDto.builder()
                .id(job.getId())

                // Employer
                .employer(job.getEmployer() != null
                                ? new SimpleUserDto(
                                job.getEmployer().getId(),
                                job.getEmployer().getFullName()
                        )
                                : null
                )

                // Company
                .company(job.getCompany() != null
                                ? new SimpleCompanyDto(
                                job.getCompany().getId(),
                                job.getCompany().getName(),
                                job.getCompany().getLogoUrl()
                        )
                                : null
                )

                // Category
                .category(job.getCategory() != null
                                ? new SimpleCategoryDto(
                                job.getCategory().getId(),
                                job.getCategory().getName()
                        )
                                : null
                )

                .title(job.getTitle())
                .description(job.getDescription())
                .jobType(job.getJobType())
                .experienceLevel(job.getExperienceLevel())

                .minSalary(job.getMinSalary())
                .maxSalary(job.getMaxSalary())
                .salaryType(job.getSalaryType())

                .location(job.getLocation())
                .remoteAllowed(job.isRemoteAllowed())
                .skills(job.getSkills())

                .postedAt(job.getPostedAt())
                .deadline(job.getDeadline())
                .status(job.getStatus())

                .viewsCount(job.getViewsCount())
                .applicantsCount(job.getApplicantsCount())

                .build();
    }
}