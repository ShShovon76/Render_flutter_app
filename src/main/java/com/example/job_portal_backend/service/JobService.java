package com.example.job_portal_backend.service;

import com.example.job_portal_backend.dtos.analytics.JobAnalyticsDto;
import com.example.job_portal_backend.dtos.job.JobCreateRequest;
import com.example.job_portal_backend.dtos.job.JobDto;
import com.example.job_portal_backend.dtos.job.JobResponseDto;
import com.example.job_portal_backend.dtos.job.JobUpdateRequest;
import com.example.job_portal_backend.dtos.searchAndPaginaton.JobSearchFilter;
import com.example.job_portal_backend.entity.*;
import com.example.job_portal_backend.enums.JobStatus;
import com.example.job_portal_backend.enums.JobType;
import com.example.job_portal_backend.exceptions.ResourceNotFoundException;
import com.example.job_portal_backend.exceptions.UnauthorizedException;
import com.example.job_portal_backend.mappers.JobMapper;
import com.example.job_portal_backend.repository.JobRepository;
import com.example.job_portal_backend.repository.JobViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class JobService {

    private final JobRepository jobRepository;
    private final JobViewRepository jobViewRepository;
    private final UserService userService;
    private final CompanyService companyService;
    private final JobCategoryService categoryService;
    private final JobMapper jobMapper;
    private final NotificationService notificationService;

    /* =========================
       READ OPERATIONS
       ========================= */

    public Page<JobResponseDto> getJobs(Pageable pageable) {
        return jobRepository.findAll(pageable)
                .map(jobMapper::toResponseDto);
    }

    public Page<JobResponseDto> searchJobs(JobSearchFilter filter, Pageable pageable) {

        Double minSalary = null;
        Double maxSalary = null;

        if (filter.getSalaryRange() != null) {
            minSalary = filter.getSalaryRange().getMin();
            maxSalary = filter.getSalaryRange().getMax();
        }

        return jobRepository.searchJobs(
                        filter.getKeyword(),
                        filter.getLocation(),
                        filter.getCategoryId(),
                        filter.getJobType(),
                        filter.getExperienceLevel(),
                        filter.getRemote(),
                        minSalary,
                        maxSalary,
                        pageable
                )
                .map(jobMapper::toResponseDto);
    }

    public JobResponseDto getJobById(Long jobId) {
        return jobMapper.toResponseDto(getJobEntity(jobId));
    }

    public Page<JobResponseDto> getJobsByEmployer(
            Long employerId,
            JobStatus status,
            JobType jobType,
            String keyword,
            Pageable pageable
    ) {
        User employer = userService.getUserEntity(employerId);

        return jobRepository.searchEmployerJobs(
                        employer,
                        status,
                        jobType,
                        keyword,
                        pageable
                )
                .map(jobMapper::toResponseDto);
    }


    public Page<JobResponseDto> getJobsByCompany(Long companyId, Pageable pageable) {
        Company company = companyService.getCompanyEntity(companyId);
        return jobRepository.findByCompany(company, pageable)
                .map(jobMapper::toResponseDto);
    }

    public Job getJobEntity(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Job not found with id: " + jobId)
                );
    }

    /* =========================
       WRITE OPERATIONS
       ========================= */

    @Transactional
    public JobResponseDto createJob(JobCreateRequest req, Long employerId) {

        User employer = userService.getUserEntity(employerId);
        Company company = companyService.getCompanyEntity(req.getCompanyId());

        if (!company.getOwner().getId().equals(employerId)) {
            throw new UnauthorizedException("Employer does not own this company");
        }

        Job job = new Job();
        job.setTitle(req.getTitle());
        job.setDescription(req.getDescription());
        job.setCompany(company);
        job.setEmployer(employer);
        job.setCategory(categoryService.getCategoryEntity(req.getCategoryId()));
        job.setJobType(req.getJobType());
        job.setExperienceLevel(req.getExperienceLevel());
        job.setMinSalary(req.getMinSalary());
        job.setMaxSalary(req.getMaxSalary());
        job.setSalaryType(req.getSalaryType());
        job.setLocation(req.getLocation());
        job.setRemoteAllowed(req.isRemoteAllowed());
        job.setSkills(req.getSkills());
        job.setDeadline(req.getDeadline());
        job.setPostedAt(LocalDateTime.now());
        job.setStatus(JobStatus.ACTIVE);
        job.setViewsCount(0);

        Job savedJob = jobRepository.save(job);

        notificationService.notifyNewJobPosted(savedJob);

        return jobMapper.toResponseDto(savedJob);
    }

    @Transactional
    public JobResponseDto updateJob(
            Long jobId,
            JobUpdateRequest req,
            Long employerId
    ) {

        Job job = getJobEntity(jobId);

        if (!job.getEmployer().getId().equals(employerId)) {
            throw new UnauthorizedException("Only job owner can update job");
        }

        if (req.getTitle() != null) job.setTitle(req.getTitle());
        if (req.getDescription() != null) job.setDescription(req.getDescription());
        if (req.getCategoryId() != null) {
            job.setCategory(categoryService.getCategoryEntity(req.getCategoryId()));
        }
        if (req.getJobType() != null) job.setJobType(req.getJobType());
        if (req.getExperienceLevel() != null) job.setExperienceLevel(req.getExperienceLevel());
        if (req.getMinSalary() != null) job.setMinSalary(req.getMinSalary());
        if (req.getMaxSalary() != null) job.setMaxSalary(req.getMaxSalary());
        if (req.getSalaryType() != null) job.setSalaryType(req.getSalaryType());
        if (req.getLocation() != null) job.setLocation(req.getLocation());
        if (req.getRemoteAllowed() != null) job.setRemoteAllowed(req.getRemoteAllowed());
        if (req.getSkills() != null) job.setSkills(req.getSkills());
        if (req.getDeadline() != null) job.setDeadline(req.getDeadline());
        if (req.getStatus() != null) job.setStatus(req.getStatus());

        return jobMapper.toResponseDto(jobRepository.save(job));
    }

    @Transactional
    public void deleteJob(Long jobId, Long employerId) {

        Job job = getJobEntity(jobId);

        if (!job.getEmployer().getId().equals(employerId)) {
            throw new UnauthorizedException("Only job owner can delete job");
        }

        jobRepository.delete(job);
        log.info("Job deleted: {}", jobId);
    }

    @Transactional
    public void recordJobView(Long jobId, Long viewerId, String ipAddress, String userAgent) {

        Job job = getJobEntity(jobId);
        User viewer = viewerId != null ? userService.getUserEntity(viewerId) : null;

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

        boolean alreadyViewed = jobViewRepository
                .existsByJobAndViewerAndViewDateAfter(job, viewer, startOfDay);

        if (!alreadyViewed) {
            jobViewRepository.save(
                    JobView.builder()
                            .job(job)
                            .viewer(viewer)
                            .ipAddress(ipAddress)
                            .userAgent(userAgent)
                            .viewDate(LocalDateTime.now())
                            .build()
            );

            job.setViewsCount(job.getViewsCount() + 1);
        }
    }

    @Transactional
    public void closeExpiredJobs() {

        List<Job> expiredJobs = jobRepository
                .findByDeadlineBeforeAndStatus(LocalDate.now(), JobStatus.ACTIVE);

        expiredJobs.forEach(job -> {
            job.setStatus(JobStatus.CLOSED);
            log.info("Job closed due to expiry: {}", job.getTitle());
        });
    }

    public JobAnalyticsDto getJobAnalytics(Long jobId) {
        Job job = getJobEntity(jobId);

        return JobAnalyticsDto.builder()
                .jobId(job.getId())
                .title(job.getTitle())
                .totalViews(job.getViewsCount())
                .uniqueViews(jobRepository.countDistinctViewersByJob(job))
                .totalApplications(job.getApplicantsCount())
                .lastViewedAt(jobRepository.findLastViewDate(job))
                .build();
    }

}
