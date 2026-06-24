package com.example.job_portal_backend.dtos.job;

import com.example.job_portal_backend.enums.ExperienceLevel;
import com.example.job_portal_backend.enums.JobStatus;
import com.example.job_portal_backend.enums.JobType;
import com.example.job_portal_backend.enums.SalaryType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDto {
    private Long id;

    @NotNull(message = "Employer ID is required")
    private Long employerId;

    @NotNull(message = "Company ID is required")
    private Long companyId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Job type is required")
    private JobType jobType;

    @NotNull(message = "Experience level is required")
    private ExperienceLevel experienceLevel;

    private Double minSalary;
    private Double maxSalary;

    @NotNull(message = "Salary type is required")
    private SalaryType salaryType;

    @NotBlank(message = "Location is required")
    private String location;

    private boolean remoteAllowed;

    private List<String> skills;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime postedAt;

    @NotNull(message = "Deadline is required")
    @Future(message = "Deadline must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;

    @NotNull(message = "Status is required")
    private JobStatus status;

    private Integer viewsCount;
    private Integer applicantsCount;

    // For response only
    private String companyName;
    private String companyLogo;
    private String categoryName;
    private String employerName;
}
