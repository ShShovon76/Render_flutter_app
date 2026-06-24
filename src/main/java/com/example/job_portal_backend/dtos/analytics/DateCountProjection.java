package com.example.job_portal_backend.dtos.analytics;

import java.time.LocalDate;

public record DateCountProjection(
        LocalDate date,
        Long count
) {}