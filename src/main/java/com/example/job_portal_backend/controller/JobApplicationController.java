package com.example.job_portal_backend.controller;

import com.example.job_portal_backend.dtos.job.JobApplicationDto;
import com.example.job_portal_backend.dtos.job.JobApplicationRequest;
import com.example.job_portal_backend.dtos.job.UpdateApplicationStatusRequest;
import com.example.job_portal_backend.entity.ApplicationStatusHistory;
import com.example.job_portal_backend.entity.User;
import com.example.job_portal_backend.security.UserPrincipal;
import com.example.job_portal_backend.service.JobApplicationService;
import com.example.job_portal_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService applicationService;
    private final UserService userService;

    @GetMapping("/id/{id}")
    public ResponseEntity<JobApplicationDto> getApplicationById(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getApplicationById(id));
    }


    @GetMapping("/{applicationId}/history")
    public ResponseEntity<List<ApplicationStatusHistory>> getApplicationHistory(
            @PathVariable Long applicationId) {
        return ResponseEntity.ok(applicationService.getApplicationHistory(applicationId));
    }


    @GetMapping("/job/{jobId}")
    public ResponseEntity<Page<JobApplicationDto>> getApplicationsByJob(
            @PathVariable Long jobId,
            Pageable pageable) {
        return ResponseEntity.ok(applicationService.getApplicationsByJob(jobId, pageable));
    }

    @GetMapping("/job-seeker/{userId}")
    public ResponseEntity<Page<JobApplicationDto>> getApplicationsByJobSeeker(
            @PathVariable Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(applicationService.getApplicationsByJobSeeker(userId, pageable));
    }

    @GetMapping("/employer/{employerId}")
    public ResponseEntity<Page<JobApplicationDto>> getApplicationsByEmployer(
            @PathVariable Long employerId,
            Pageable pageable) {
        return ResponseEntity.ok(applicationService.getApplicationsByEmployer(employerId, pageable));
    }

    @PostMapping("/apply/{jobId}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<JobApplicationDto> apply(
            @PathVariable Long jobId,
            @RequestBody @Valid JobApplicationRequest request,
            Authentication authentication
    ) throws AccessDeniedException {
        User user = userService.getCurrentUser(authentication);

        return ResponseEntity.ok(
                applicationService.apply(
                        jobId,
                        user.getId(),
                        request.getResumeId(),
                        request.getCoverLetter()
                )
        );
    }
    @GetMapping("/jobs/{jobId}/applied")
    public ResponseEntity<Boolean> hasApplied(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(
                applicationService.hasUserApplied(jobId, user.getId())
        );
    }

    @PutMapping("/{applicationId}/status")
    public ResponseEntity<JobApplicationDto> updateApplicationStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody UpdateApplicationStatusRequest request,
            @RequestParam Long changedByUserId) {
        return ResponseEntity.ok(applicationService.updateApplicationStatus(applicationId, request, changedByUserId));
    }

    @GetMapping("/job/{jobId}/count")
    public ResponseEntity<Long> countApplicationsByJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.countApplicationsByJob(jobId));
    }

    @DeleteMapping("/{applicationId}")
    public ResponseEntity<Void> withdrawApplication(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        applicationService.withdrawApplication(applicationId, user.getId());
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/employer/recent")
    public ResponseEntity<Page<JobApplicationDto>> getRecentApplications(
            @RequestParam Long employerId,
            Pageable pageable) {

        return ResponseEntity.ok(
                applicationService.getRecentApplicationsForEmployer(employerId, pageable)
        );
    }

}
