package com.example.job_portal_backend.dtos.analytics;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteMetricsResponse {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    // User metrics
    private Long totalUsers;
    private Long newUsersToday;
    private Long newUsersThisWeek;
    private Long newUsersThisMonth;
    private Map<String, Long> usersByRole;

    // Job metrics
    private Long totalJobs;
    private Long activeJobs;
    private Long jobsPostedToday;
    private Long jobsPostedThisWeek;

    // Company metrics
    private Long totalCompanies;
    private Long verifiedCompanies;
    private Long companiesCreatedToday;

    // Application metrics
    private Long totalApplications;
    private Long applicationsToday;
    private Long applicationsThisWeek;

    // Subscription metrics
    private Long activeSubscriptions;
    private Long subscriptionsToday;

    // Category metrics
    private List<JobCategoryStats> popularCategories;
}

