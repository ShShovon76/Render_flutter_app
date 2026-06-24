package com.example.job_portal_backend.dtos.analytics;

import com.example.job_portal_backend.enums.ApplicationStatus;

public record StatusCountProjection(
        ApplicationStatus status,
        Long count
) {}

