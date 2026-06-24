package com.example.job_portal_backend.dtos.job;

import com.example.job_portal_backend.enums.ExperienceLevel;
import com.example.job_portal_backend.enums.JobStatus;
import com.example.job_portal_backend.enums.JobType;
import com.example.job_portal_backend.enums.SalaryType;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class JobUpdateRequest {

    private String title;
    private String description;
    private Long categoryId;
    private JobType jobType;
    private ExperienceLevel experienceLevel;

    private Double minSalary;
    private Double maxSalary;
    private SalaryType salaryType;

    private String location;
    private Boolean remoteAllowed;
    private List<String> skills;

    private LocalDate deadline;
    private JobStatus status;
}

