package com.example.job_portal_backend.service;

import com.example.job_portal_backend.dtos.job.ResumeDto;
import com.example.job_portal_backend.entity.JobSeekerProfile;
import com.example.job_portal_backend.entity.Resume;
import com.example.job_portal_backend.exceptions.ResourceNotFoundException;
import com.example.job_portal_backend.mappers.ResumeMapper;
import com.example.job_portal_backend.repository.JobApplicationRepository;
import com.example.job_portal_backend.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final JobSeekerProfileService profileService;
    private final FileStorageService fileStorageService;
    private final ResumeMapper resumeMapper;
    private final JobApplicationRepository jobApplicationRepository;

    public List<ResumeDto> getResumes(Long userId) {
        JobSeekerProfile profile = profileService.getOrCreateProfileEntity(userId);

        return resumeRepository.findByJobSeeker(profile)
                .stream()
                .map(resumeMapper::toDto)
                .toList();
    }

    public ResumeDto uploadResume(Long userId, MultipartFile file, String title) throws IOException {
        JobSeekerProfile profile = profileService.getOrCreateProfileEntity(userId);

        String path = fileStorageService.storeResume(file);

        Resume resume = Resume.builder()
                .title(title != null ? title : file.getOriginalFilename())
                .fileUrl(fileStorageService.getFileUrl(path))
                .originalFileName(file.getOriginalFilename())
                .jobSeeker(profile)
                .primaryResume(false)
                .build();

        return resumeMapper.toDto(resumeRepository.save(resume));
    }

    public void setPrimary(Long userId, Long resumeId) {
        JobSeekerProfile profile = profileService.getOrCreateProfileEntity(userId);

        resumeRepository.findByJobSeeker(profile)
                .forEach(r -> r.setPrimaryResume(r.getId().equals(resumeId)));
    }

    @Transactional
    public void deleteResume(Long userId, Long resumeId) throws IOException {

        JobSeekerProfile profile = profileService.getOrCreateProfileEntity(userId);

        Resume resume = resumeRepository
                .findByIdAndJobSeeker(resumeId, profile)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        if (jobApplicationRepository.existsByResume(resume)) {
            throw new IllegalStateException("Resume is already used in job applications");
        }

        if (resume.getFileUrl() != null) {
            fileStorageService.deleteFile(resume.getFileUrl());
        }

        resumeRepository.delete(resume);
    }
    @Transactional(readOnly = true)
    public Resume getResumeAccessibleByUser(Long resumeId, Long userId) {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));


        if (resume.getJobSeeker().getUser().getId().equals(userId)) {
            return resume;
        }

        // Case 2: employer who received this resume via application
        boolean employerAllowed =
                jobApplicationRepository.employerCanAccessResume(resumeId, userId);

        if (employerAllowed) {
            return resume;
        }

        throw new AccessDeniedException("You are not allowed to access this resume");
    }

}


