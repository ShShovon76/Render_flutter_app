package com.example.job_portal_backend.controller;

import com.example.job_portal_backend.dtos.other.SavedJobDto;
import com.example.job_portal_backend.security.UserPrincipal;
import com.example.job_portal_backend.service.SavedJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/saved-jobs")
@RequiredArgsConstructor
public class SavedJobController {

    private final SavedJobService savedJobService;

    @GetMapping
    public ResponseEntity<Page<SavedJobDto>> getSavedJobs(
            @AuthenticationPrincipal UserPrincipal principal,
            Pageable pageable
    ) {
        Page<SavedJobDto> page = savedJobService.getSavedJobs(principal.getId(), pageable);

        return ResponseEntity.ok(page);
    }



    @GetMapping("/check")
    public ResponseEntity<Boolean> isJobSaved(
            @RequestParam Long jobId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(
                savedJobService.isJobSaved(jobId, principal.getId())
        );
    }


    @PostMapping
    public ResponseEntity<SavedJobDto> saveJob(
            @RequestParam Long jobId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedJobService.saveJob(jobId, principal.getId()));
    }


    @DeleteMapping("/unsave")
    public ResponseEntity<Void> unsaveJob(
            @RequestParam Long jobId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        savedJobService.unsaveJob(jobId, principal.getId());
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{savedJobId}")
    public ResponseEntity<Void> unsaveJobById(
            @PathVariable Long savedJobId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        savedJobService.unsaveJobById(savedJobId, principal.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{jobId}/count")
    public ResponseEntity<Long> getSaveCountForJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(savedJobService.getSaveCountForJob(jobId));
    }
}
