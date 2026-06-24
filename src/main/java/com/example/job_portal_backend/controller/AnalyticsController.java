package com.example.job_portal_backend.controller;

import com.example.job_portal_backend.dtos.analytics.*;
import com.example.job_portal_backend.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Analytics", description = "Analytics and dashboard endpoints")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Operation(summary = "Get job views analytics",
            description = "Get view statistics for a specific job. Employer or admin only.")
    @GetMapping("/jobs/{jobId}/views")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<JobViewsResponse> getJobViews(
            @PathVariable @NotNull Long jobId,
            @RequestParam(required = false)
            @Parameter(description = "Start date (ISO format: yyyy-MM-dd'T'HH:mm:ss)")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,

            @RequestParam(required = false)
            @Parameter(description = "End date (ISO format: yyyy-MM-dd'T'HH:mm:ss)")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to) {

        log.info("Getting job views analytics for jobId: {}, from: {}, to: {}", jobId, from, to);
        JobViewsResponse response = analyticsService.getJobViews(jobId, from, to);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Get application trends",
            description = "Get application trends for a job, employer, or overall.")
    @GetMapping("/applications/trends")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApplicationTrendsResponse> getApplicationTrends(
            @RequestParam(required = false)
            @Parameter(description = "Job ID for filtering")
            Long jobId,

            @RequestParam(required = false)
            @Parameter(description = "Employer ID for filtering")
            Long employerId,

            @RequestParam(required = false)
            @Parameter(description = "Start date (ISO format: yyyy-MM-dd'T'HH:mm:ss)")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,

            @RequestParam(required = false)
            @Parameter(description = "End date (ISO format: yyyy-MM-dd'T'HH:mm:ss)")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to) {

        log.info("Getting application trends - jobId: {}, employerId: {}, from: {}, to: {}",
                jobId, employerId, from, to);

        ApplicationTrendsResponse response = analyticsService.getApplicationTrends(jobId, employerId, from, to);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Get employer dashboard",
            description = "Get comprehensive dashboard data for an employer.")
    @GetMapping("/employers/{employerId}/dashboard")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<EmployerDashboardResponse> getEmployerDashboard(
            @PathVariable @NotNull Long employerId) {

        log.info("Getting employer dashboard for employerId: {}", employerId);
        EmployerDashboardResponse response = analyticsService.getEmployerDashboard(employerId);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Get site metrics",
            description = "Get site-wide metrics and statistics. Admin only.")
    @GetMapping("/site-metrics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SiteMetricsResponse> getSiteMetrics() {
        log.info("Getting site metrics");
        SiteMetricsResponse response = analyticsService.getSiteMetrics();
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Get job seeker dashboard",
            description = "Get dashboard data for a job seeker.")
    @GetMapping("/job-seekers/{jobSeekerId}/dashboard")
    @PreAuthorize("hasAnyRole('JOB_SEEKER', 'ADMIN')")
    public ResponseEntity<JobSeekerDashboardResponse> getJobSeekerDashboard(
            @PathVariable @NotNull Long jobSeekerId) {

        log.info("Getting job seeker dashboard for jobSeekerId: {}", jobSeekerId);
        // You need to implement this method in AnalyticsService first
        JobSeekerDashboardResponse response = new JobSeekerDashboardResponse();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/test-date")
    public ResponseEntity<String> testDateParsing(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return ResponseEntity.ok("Date parsed successfully: " + date);
    }

}
