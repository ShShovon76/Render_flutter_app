package com.example.job_portal_backend.service;

import com.example.job_portal_backend.dtos.other.SavedJobDto;
import com.example.job_portal_backend.entity.Job;
import com.example.job_portal_backend.entity.JobSeekerProfile;
import com.example.job_portal_backend.entity.SavedJob;
import com.example.job_portal_backend.exceptions.ResourceNotFoundException;
import com.example.job_portal_backend.mappers.SavedJobMapper;
import com.example.job_portal_backend.repository.SavedJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final JobService jobService;
    private final JobSeekerProfileService profileService;
    private final SavedJobMapper savedJobMapper;
    private final NotificationService notificationService;

    public Page<SavedJobDto> getSavedJobs(Long userId, Pageable pageable) {
        JobSeekerProfile profile = profileService.getOrCreateProfileEntity(userId);
        return savedJobRepository.findByJobSeeker(profile, pageable)
                .map(savedJobMapper::toDto);
    }


    public boolean isJobSaved(Long jobId, Long userId) {
        JobSeekerProfile profile = profileService.getOrCreateProfileEntity(userId);
        Job job = jobService.getJobEntity(jobId);

        return savedJobRepository.existsByJobAndJobSeeker(job, profile);
    }

    @Transactional
    public SavedJobDto saveJob(Long jobId, Long userId) {

        JobSeekerProfile profile = profileService.getOrCreateProfileEntity(userId);
        Job job = jobService.getJobEntity(jobId);

        if (savedJobRepository.existsByJobAndJobSeeker(job, profile)) {
            throw new IllegalArgumentException("Job already saved");
        }

        SavedJob savedJob = SavedJob.builder()
                .job(job)
                .jobSeeker(profile)
                .build();

        SavedJob saved = savedJobRepository.save(savedJob);

        return savedJobMapper.toDto(saved);
    }

    @Transactional
    public void unsaveJob(Long jobId, Long userId) {
        JobSeekerProfile profile = profileService.getOrCreateProfileEntity(userId);
        Job job = jobService.getJobEntity(jobId);

        savedJobRepository.deleteByJobAndJobSeeker(job, profile);
        log.info("Job unsaved: {} by user: {}", jobId, userId);
    }

    @Transactional
    public void unsaveJobById(Long savedJobId, Long userId) {
        SavedJob savedJob = savedJobRepository.findById(savedJobId)
                .orElseThrow(() -> new ResourceNotFoundException("Saved job not found with id: " + savedJobId));

        // Verify ownership
        if (!savedJob.getJobSeeker().getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Saved job not found for user");
        }

        savedJobRepository.delete(savedJob);
        log.info("Saved job removed: {} by user: {}", savedJobId, userId);
    }

    public long getSaveCountForJob(Long jobId) {
        Job job = jobService.getJobEntity(jobId);
        return savedJobRepository.countByJob(job);
    }
}
