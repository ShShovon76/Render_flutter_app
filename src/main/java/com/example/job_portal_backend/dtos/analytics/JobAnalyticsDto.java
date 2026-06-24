package com.example.job_portal_backend.dtos.analytics;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class JobAnalyticsDto {

    private Long jobId;
    private String title;

    private int totalViews;
    private int uniqueViews;
    private int totalApplications;

    private LocalDateTime lastViewedAt;
}

