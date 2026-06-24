package com.example.job_portal_backend.dtos.job;

import com.example.job_portal_backend.enums.ExperienceLevel;
import com.example.job_portal_backend.enums.JobType;
import com.example.job_portal_backend.enums.SalaryType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
public class JobCreateRequest {

    @NotNull
    private Long companyId;

    @NotNull
    private Long categoryId;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private JobType jobType;

    @NotNull
    private ExperienceLevel experienceLevel;

    private Double minSalary;
    private Double maxSalary;

    @NotNull
    private SalaryType salaryType;

    @NotBlank
    private String location;

    private boolean remoteAllowed;

    private List<String> skills;

    @Future
    private LocalDate deadline;
}
