package com.example.job_portal_backend.controller;

import com.example.job_portal_backend.dtos.analytics.JobAnalyticsDto;
import com.example.job_portal_backend.dtos.job.*;
import com.example.job_portal_backend.dtos.searchAndPaginaton.JobSearchFilter;
import com.example.job_portal_backend.enums.JobStatus;
import com.example.job_portal_backend.enums.JobType;
import com.example.job_portal_backend.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Validated
public class JobController {

    private final JobService jobService;

    /* =========================
       PUBLIC JOB APIs
       ========================= */

    // List jobs (public)
    @GetMapping
    public ResponseEntity<Page<JobResponseDto>> getJobs(Pageable pageable) {
        return ResponseEntity.ok(jobService.getJobs(pageable));
    }

    // Search jobs (optimized POST)
    @PostMapping("/search")
    public ResponseEntity<Page<JobResponseDto>> searchJobs(
            @RequestBody JobSearchFilter filter,
            @PageableDefault(size = 10, sort = "postedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(jobService.searchJobs(filter, pageable));
    }

    // Job details
    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponseDto> getJobById(@PathVariable Long jobId) {
        return ResponseEntity.ok(jobService.getJobById(jobId));
    }

    // Jobs by company
    @GetMapping("/company/{companyId}")
    public ResponseEntity<Page<JobResponseDto>> getJobsByCompany(
            @PathVariable Long companyId,
            @PageableDefault(size = 10, sort = "postedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(jobService.getJobsByCompany(companyId, pageable));
    }

    /* =========================
       EMPLOYER JOB APIs
       ========================= */


    @GetMapping("/employer/{employerId}")
    public ResponseEntity<Page<JobResponseDto>> getJobsByEmployer(
            @PathVariable Long employerId,
            @RequestParam(required = false) JobStatus status,
            @RequestParam(required = false) JobType jobType,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "postedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                jobService.getJobsByEmployer(employerId, status, jobType, keyword, pageable)
        );
    }

    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<JobResponseDto> createJob(
            @Valid @RequestBody JobCreateRequest request,
            @RequestParam Long employerId
    ) {
        System.out.println("CREATE JOB - Employer ID: {}"+ employerId);
        System.out.println("CREATE JOB - Request: {}"+ request);
        System.out.println("CREATE JOB - Company ID: {}"+ request.getCompanyId());
        System.out.println("CREATE JOB - Category ID: {}"+ request.getCategoryId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobService.createJob(request, employerId));
    }

    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    @PutMapping("/{jobId}")
    public ResponseEntity<JobResponseDto> updateJob(
            @PathVariable Long jobId,
            @RequestBody JobUpdateRequest request,
            @RequestParam Long employerId
    ) {
        return ResponseEntity.ok(
                jobService.updateJob(jobId, request, employerId)
        );
    }

    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long jobId,
            @RequestParam Long employerId
    ) {
        jobService.deleteJob(jobId, employerId);
        return ResponseEntity.noContent().build();
    }

    /* =========================
       JOB ANALYTICS
       ========================= */

    @PostMapping("/{jobId}/view")
    public ResponseEntity<Void> recordJobView(
            @PathVariable Long jobId,
            @RequestBody JobViewRequest request
    ) {
        jobService.recordJobView(
                jobId,
                request.getViewerId(),
                request.getIpAddress(),
                request.getUserAgent()
        );
        return ResponseEntity.ok().build();
    }

    // Employer job analytics
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    @GetMapping("/{jobId}/analytics")
    public ResponseEntity<JobAnalyticsDto> getJobAnalytics(
            @PathVariable Long jobId
    ) {
        return ResponseEntity.ok(jobService.getJobAnalytics(jobId));
    }

    /* =========================
       ADMIN APIs
       ========================= */

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/close-expired")
    public ResponseEntity<Void> closeExpiredJobs() {
        jobService.closeExpiredJobs();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<JobResponseDto>> searchJobsGet(
            JobSearchFilter filter,
            @PageableDefault(size = 10, sort = "postedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(jobService.searchJobs(filter, pageable));
    }

}

