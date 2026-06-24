package com.example.job_portal_backend.controller;

import com.example.job_portal_backend.dtos.job.ResumeDto;
import com.example.job_portal_backend.entity.Resume;
import com.example.job_portal_backend.security.UserPrincipal;
import com.example.job_portal_backend.service.FileStorageService;
import com.example.job_portal_backend.service.ResumeService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final FileStorageService fileStorageService;

    // ===================== GET MY RESUMES =====================
    @GetMapping
    public ResponseEntity<List<ResumeDto>> getResumes(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(resumeService.getResumes(user.getId()));
    }

    // ===================== UPLOAD =====================
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeDto> uploadResume(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam MultipartFile file,
            @RequestParam(required = false) String title
    ) throws IOException {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resumeService.uploadResume(user.getId(), file, title));
    }

    // ===================== SET PRIMARY =====================
    @PutMapping("/{resumeId}/primary")
    public ResponseEntity<Void> setPrimary(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long resumeId
    ) {
        resumeService.setPrimary(user.getId(), resumeId);
        return ResponseEntity.ok().build();
    }

    // ===================== DELETE =====================
    @DeleteMapping("/{resumeId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long resumeId
    ) throws IOException {

        resumeService.deleteResume(user.getId(), resumeId);
        return ResponseEntity.noContent().build();
    }

    // ===================== DOWNLOAD =====================
    @GetMapping("/{resumeId}/download")
    public ResponseEntity<byte[]> downloadResume(
            @PathVariable Long resumeId,
            @AuthenticationPrincipal UserPrincipal user
    ) throws IOException {
        if (user == null) {
            throw new AccessDeniedException("Unauthorized");
        }
        Resume resume = resumeService.getResumeAccessibleByUser(
                resumeId,
                user.getId()
        );

        byte[] file = fileStorageService.loadFile(resume.getFileUrl());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resume.getOriginalFileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }
}


