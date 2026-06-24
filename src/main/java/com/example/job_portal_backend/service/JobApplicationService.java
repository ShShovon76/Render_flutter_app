package com.example.job_portal_backend.service;

import com.example.job_portal_backend.config.ConflictException;
import com.example.job_portal_backend.dtos.job.JobApplicationDto;
import com.example.job_portal_backend.dtos.job.UpdateApplicationStatusRequest;
import com.example.job_portal_backend.entity.*;
import com.example.job_portal_backend.enums.ApplicationStatus;
import com.example.job_portal_backend.exceptions.ResourceNotFoundException;
import com.example.job_portal_backend.exceptions.UnauthorizedException;
import com.example.job_portal_backend.mappers.JobApplicationMapper;
import com.example.job_portal_backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final JobSeekerProfileRepository profileRepository;
    private final JobApplicationMapper applicationMapper;
    private final NotificationService notificationService;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final JobApplicationMapper mapper;

    public JobApplicationDto getApplicationById(Long id) {
        JobApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        return applicationMapper.toDto(application);
    }

    public List<ApplicationStatusHistory> getApplicationHistory(Long applicationId) {
        return applicationRepository.findHistoryByApplicationId(applicationId);
    }


    public Page<JobApplicationDto> getApplicationsByJob(Long jobId, Pageable pageable) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        return applicationRepository.findByJob(job, pageable)
                .map(applicationMapper::toDto);
    }

    public Page<JobApplicationDto> getApplicationsByJobSeeker(Long userId, Pageable pageable) {
        JobSeekerProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker profile not found for user: " + userId));

        return applicationRepository.findByJobSeeker(profile, pageable)
                .map(applicationMapper::toDto);
    }

    public Page<JobApplicationDto> getApplicationsByEmployer(Long employerId, Pageable pageable) {
        return applicationRepository
                .findByJob_Employer_IdOrderByAppliedAtDesc(employerId, pageable)
                .map(applicationMapper::toDto);
    }


    @Transactional
    public JobApplicationDto apply(
            Long jobId,
            Long userId,
            Long resumeId,
            String coverLetter
    ) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        JobSeekerProfile jobSeeker = jobSeekerProfileRepository
                .findByUser(user)
                .orElseGet(() -> {
                    JobSeekerProfile p = new JobSeekerProfile();
                    p.setUser(user);
                    return jobSeekerProfileRepository.save(p);
                });

        Resume resume = resumeRepository
                .findByIdAndJobSeeker(resumeId, jobSeeker)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        if (applicationRepository.existsByJobIdAndJobSeeker_User_Id(job.getId(), user.getId())) {
            throw new ConflictException("You have already applied for this job");
        }


        JobApplication application = JobApplication.builder()
                .job(job)
                .jobSeeker(jobSeeker)
                .resume(resume)
                .coverLetter(coverLetter)
                .status(ApplicationStatus.APPLIED)
                .appliedAt(LocalDateTime.now())
                .build();
        JobApplication saved = applicationRepository.save(application);

        notificationService.createApplicationNotification(
                saved.getJob().getEmployer(),
                saved,
                "New application received for: " + saved.getJob().getTitle()
        );

        return applicationMapper.toDto(saved);
    }


    public boolean hasUserApplied(Long jobId, Long userId) {
        return applicationRepository.existsByJobIdAndJobSeeker_User_Id(jobId, userId);
    }


    @Transactional
    public JobApplicationDto updateApplicationStatus(Long applicationId, UpdateApplicationStatusRequest request, Long changedByUserId) {
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        // Verify user has permission (employer or admin)
        User changedBy = new User();
        changedBy.setId(changedByUserId);

        if (!isAuthorizedToUpdateStatus(application, changedBy)) {
            throw new UnauthorizedException("You are not authorized to update this application status");
        }

        ApplicationStatus oldStatus = application.getStatus();
        application.setStatus(request.getStatus());

        JobApplication updatedApplication = applicationRepository.save(application);

        // Create status history
        createStatusHistory(updatedApplication, oldStatus, request.getStatus(), "Status updated");

        // Send notification to job Seeker
        notificationService.createStatusUpdateNotification(
                updatedApplication.getJobSeeker().getUser(),
                updatedApplication,
                "Application status updated to " + request.getStatus()
        );

        log.info("Application status updated: {} -> {} for application: {}",
                oldStatus, request.getStatus(), applicationId);

        return applicationMapper.toDto(updatedApplication);
    }

    private void createStatusHistory(JobApplication application, ApplicationStatus fromStatus,
                                     ApplicationStatus toStatus, String note) {
        ApplicationStatusHistory history = ApplicationStatusHistory.builder()
                .application(application)
                .fromStatus(fromStatus != null ? fromStatus : toStatus)
                .toStatus(toStatus)
                .note(note)
                .changedAt(LocalDateTime.now())
                .changedBy(application.getJob().getEmployer())
                .build();

        application.getStatusHistory().add(history);
    }

    private boolean isAuthorizedToUpdateStatus(JobApplication application, User user) {

        if (application.getJob().getEmployer().getId().equals(user.getId())) {
            return true;
        }

        // Admin can update status
        User employer = application.getJob().getEmployer();

        return false;
    }

    public long countApplicationsByJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        return applicationRepository.countApplicationsByJob(job);
    }

    @Transactional
    public void withdrawApplication(Long applicationId, Long userId) {

        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));

        Long ownerId = application.getJobSeeker().getUser().getId();

        if (!ownerId.equals(userId)) {
            throw new AccessDeniedException("You are not allowed to withdraw this application");
        }

        if (application.getStatus() == ApplicationStatus.OFFERED) {
            throw new IllegalStateException("You cannot withdraw after an offer is made");
        }

        // ✅ Notify employer BEFORE delete
        notificationService.createApplicationNotification(
                application.getJob().getEmployer(),
                application,
                "A candidate has withdrawn their application for: " +
                        application.getJob().getTitle()
        );

        applicationRepository.delete(application);
    }


    public Page<JobApplicationDto> getRecentApplicationsForEmployer(Long employerId, Pageable pageable) {
        return applicationRepository.findRecentByEmployer(employerId, pageable)
                .map(applicationMapper::toDto);
    }
}
