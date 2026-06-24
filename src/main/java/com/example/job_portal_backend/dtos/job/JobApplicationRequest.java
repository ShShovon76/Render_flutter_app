package com.example.job_portal_backend.dtos.job;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JobApplicationRequest {
    @NotNull
    private Long resumeId;

    private String coverLetter;
}

