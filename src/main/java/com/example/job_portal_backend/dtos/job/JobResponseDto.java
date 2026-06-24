package com.example.job_portal_backend.dtos.job;

import com.example.job_portal_backend.enums.ExperienceLevel;
import com.example.job_portal_backend.enums.JobStatus;
import com.example.job_portal_backend.enums.JobType;
import com.example.job_portal_backend.enums.SalaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobResponseDto {

    private Long id;

    private SimpleUserDto employer;
    private SimpleCompanyDto company;
    private SimpleCategoryDto category;

    private String title;
    private String description;

    private JobType jobType;
    private ExperienceLevel experienceLevel;

    private Double minSalary;
    private Double maxSalary;
    private SalaryType salaryType;

    private String location;
    private boolean remoteAllowed;

    private List<String> skills;

    private LocalDateTime postedAt;
    private LocalDate deadline;

    private JobStatus status;

    private Integer viewsCount;
    private Integer applicantsCount;
}