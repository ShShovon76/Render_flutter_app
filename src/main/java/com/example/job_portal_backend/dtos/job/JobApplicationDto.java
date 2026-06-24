package com.example.job_portal_backend.dtos.job;

import com.example.job_portal_backend.enums.ApplicationStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationDto {

    private Long id;

    @NotNull
    private Long jobId;

    @NotNull
    private Long jobSeekerId;

    // 🔥 resume reference
    private Long resumeId;
    private String resumeUrl;
    private String resumeTitle;

    private String coverLetter;

    private ApplicationStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime appliedAt;

    // display-only
    private String jobTitle;
    private String companyName;
    private String jobSeekerName;
    private String jobSeekerEmail;
    private String jobSeekerProfilePicture;
}

