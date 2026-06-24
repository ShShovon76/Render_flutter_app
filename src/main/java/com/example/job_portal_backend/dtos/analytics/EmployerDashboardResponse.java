package com.example.job_portal_backend.dtos.analytics;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployerDashboardResponse {
    private Long employerId;
    private String employerName;
    private String companyName; // ✅ Keep

    // Add missing profileViews field
    private Long profileViews = 0L; // ✅ ADD THIS

    private Long totalJobs = 0L;
    private Long activeJobs = 0L;
    private Long totalApplications = 0L;
    private Long recentApplicationsCount = 0L; // Last 30 days

    private List<JobViewStats> topViewedJobs = new ArrayList<>();
    private Map<String, Long> applicationStatusBreakdown = new HashMap<>();
    private boolean hasActiveSubscription = false;
}
