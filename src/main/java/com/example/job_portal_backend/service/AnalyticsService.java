package com.example.job_portal_backend.service;

import com.example.job_portal_backend.dtos.analytics.*;
import com.example.job_portal_backend.entity.Company;
import com.example.job_portal_backend.entity.Job;
import com.example.job_portal_backend.entity.JobSeekerProfile;
import com.example.job_portal_backend.entity.User;
import com.example.job_portal_backend.enums.ApplicationStatus;
import com.example.job_portal_backend.enums.JobStatus;
import com.example.job_portal_backend.enums.UserRole;
import com.example.job_portal_backend.exceptions.ResourceNotFoundException;
import com.example.job_portal_backend.exceptions.UnauthorizedException;
import com.example.job_portal_backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final JobRepository jobRepository;
    private final JobViewRepository jobViewRepository;
    private final JobApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final JobCategoryRepository categoryRepository;

    /* =========================================================
       Helpers
       ========================================================= */

    private User currentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private LocalDateTime from(LocalDateTime from) {
        return from != null ? from : LocalDateTime.now().minusDays(30);
    }

    private LocalDateTime to(LocalDateTime to) {
        return to != null ? to : LocalDateTime.now();
    }

    private Map<String, Long> statusBreakdown(Long jobId, Long employerId) {
        return applicationRepository
                .getApplicationStatusBreakdown(jobId, employerId)
                .stream()
                .collect(Collectors.toMap(
                        p -> p.status().name(),
                        StatusCountProjection::count
                ));
    }

    private List<DailyApplicationCount> dailyApplications(
            Long jobId,
            Long employerId,
            LocalDateTime from,
            LocalDateTime to
    ) {
        return applicationRepository
                .getDailyApplicationCounts(jobId, employerId, from, to)
                .stream()
                .map(p -> new DailyApplicationCount(p.date(), p.count()))
                .sorted(Comparator.comparing(DailyApplicationCount::getDate))
                .toList();
    }

    /* =========================================================
       1. Job Views Analytics
       ========================================================= */

    public JobViewsResponse getJobViews(Long jobId,
                                        LocalDateTime from,
                                        LocalDateTime to) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        User user = currentUser();

        if (user.getRole() != UserRole.ADMIN &&
                !job.getEmployer().getId().equals(user.getId())) {
            throw new UnauthorizedException("Not authorized");
        }

        from = from(from);
        to = to(to);

        return JobViewsResponse.builder()
                .jobId(jobId)
                .jobTitle(job.getTitle())
                .companyName(job.getCompany().getName())
                .fromDate(from)
                .toDate(to)
                .totalViews(
                        jobViewRepository.countByJobAndViewDateBetween(job, from, to)
                )
                .uniqueViews(
                        jobViewRepository.countDistinctViewersByJobAndDateRange(jobId, from, to)
                )
                .dailyViews(
                        jobViewRepository.getDailyViewCounts(jobId, from, to)
                                .stream()
                                .map(p -> new DailyViewCount(p.date(), p.count()))
                                .sorted(Comparator.comparing(DailyViewCount::getDate))
                                .toList()
                )
                .build();
    }

    /* =========================================================
       2. Application Trends
       ========================================================= */

    public ApplicationTrendsResponse getApplicationTrends(
            Long jobId,
            Long employerId,
            LocalDateTime from,
            LocalDateTime to) {

        if (jobId != null && employerId != null) {
            throw new IllegalArgumentException("Provide either jobId or employerId");
        }

        User user = currentUser();
        from = from(from);
        to = to(to);

        ApplicationTrendsResponse.ApplicationTrendsResponseBuilder builder =
                ApplicationTrendsResponse.builder()
                        .fromDate(from)
                        .toDate(to);

        if (jobId != null) {
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

            if (user.getRole() != UserRole.ADMIN &&
                    !job.getEmployer().getId().equals(user.getId())) {
                throw new UnauthorizedException("Not authorized");
            }

            return builder
                    .jobId(jobId)
                    .jobTitle(job.getTitle())
                    .employerId(job.getEmployer().getId())
                    .employerName(job.getEmployer().getFullName())
                    .targetName(job.getTitle())
                    .dailyApplications(dailyApplications(jobId, null, from, to))
                    .statusBreakdown(statusBreakdown(jobId, null))
                    .totalApplications(applicationRepository.countApplicationsByJob(job))
                    .build();
        }

        if (employerId != null) {
            if (user.getRole() != UserRole.ADMIN &&
                    !user.getId().equals(employerId)) {
                throw new UnauthorizedException("Not authorized");
            }

            User employer = userRepository.findById(employerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

            return builder
                    .employerId(employerId)
                    .employerName(employer.getFullName())
                    .targetName(employer.getFullName())
                    .dailyApplications(dailyApplications(null, employerId, from, to))
                    .statusBreakdown(statusBreakdown(null, employerId))
                    .totalApplications(applicationRepository.countByEmployer(employer))
                    .build();
        }

        if (user.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("Admin only");
        }

        return builder
                .targetName("All Applications")
                .dailyApplications(dailyApplications(null, null, from, to))
                .statusBreakdown(statusBreakdown(null, null))
                .totalApplications(applicationRepository.count())
                .build();
    }

    /* =========================================================
       3. Employer Dashboard
       ========================================================= */

    public EmployerDashboardResponse getEmployerDashboard(Long employerId) {

        User employer = userRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

        User user = currentUser();

        if (user.getRole() != UserRole.ADMIN &&
                !user.getId().equals(employerId)) {
            throw new UnauthorizedException("Not authorized");
        }

        Pageable top5 = PageRequest.of(0, 5);

        return EmployerDashboardResponse.builder()
                .employerId(employerId)
                .employerName(employer.getFullName())
                .companyName(
                        employer.getOwnedCompanies().stream()
                                .findFirst()
                                .map(Company::getName)
                                .orElse("No Company")
                )
                .totalJobs(jobRepository.countByEmployer(employer))
                .activeJobs(jobRepository.countByEmployerAndStatus(employer, JobStatus.ACTIVE))
                .totalApplications(applicationRepository.countByEmployer(employer))
                .recentApplicationsCount(
                        applicationRepository.countByEmployerAndAppliedAtAfter(
                                employer,
                                LocalDateTime.now().minusDays(30)
                        )
                )
                .topViewedJobs(
                        jobRepository.findTopViewedJobsByEmployer(employer, top5)
                                .getContent()
                                .stream()
                                .map(job -> JobViewStats.builder()
                                        .jobId(job.getId())
                                        .jobTitle(job.getTitle())
                                        .views(
                                                job.getViewsCount() != null
                                                        ? job.getViewsCount()
                                                        : 0
                                        )
                                        .applicants(
                                                job.getApplications() != null
                                                        ? job.getApplications().size()
                                                        : 0
                                        )
                                        .build())
                                .toList()
                )
                .applicationStatusBreakdown(
                        statusBreakdown(null, employerId)
                )
                .hasActiveSubscription(
                        subscriptionRepository.findActiveSubscriptionByUser(employer).isPresent()
                )
                .build();
    }

    /* =========================================================
       4. Site Metrics (Admin)
       ========================================================= */

    public SiteMetricsResponse getSiteMetrics() {

        User user = currentUser();

        if (user.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("Admin only");
        }

        LocalDateTime now = LocalDateTime.now();

        return SiteMetricsResponse.builder()
                .timestamp(now)
                .totalUsers(userRepository.count())
                .newUsersToday(userRepository.countByCreatedAtAfter(now.minusDays(1)))
                .newUsersThisWeek(userRepository.countByCreatedAtAfter(now.minusDays(7)))
                .newUsersThisMonth(userRepository.countByCreatedAtAfter(now.minusDays(30)))
                .usersByRole(
                        Arrays.stream(UserRole.values())
                                .collect(Collectors.toMap(
                                        UserRole::name,
                                        userRepository::countByRole
                                ))
                )
                .totalJobs(jobRepository.count())
                .activeJobs(jobRepository.countByStatus(JobStatus.ACTIVE))
                .jobsPostedToday(jobRepository.countByPostedAtAfter(now.minusDays(1)))
                .jobsPostedThisWeek(jobRepository.countByPostedAtAfter(now.minusDays(7)))
                .totalCompanies(companyRepository.count())
                .verifiedCompanies(companyRepository.countByVerified(true))
                .companiesCreatedToday(companyRepository.countByCreatedAtAfter(now.minusDays(1)))
                .totalApplications(applicationRepository.count())
                .applicationsToday(applicationRepository.countByAppliedAtAfter(now.minusDays(1)))
                .applicationsThisWeek(applicationRepository.countByAppliedAtAfter(now.minusDays(7)))
                .activeSubscriptions(subscriptionRepository.countActiveSubscriptions())
                .subscriptionsToday(subscriptionRepository.countByCreatedAtAfter(now.minusDays(1)))
                .popularCategories(
                        categoryRepository.findTopCategoriesByJobCount(PageRequest.of(0, 5))
                                .stream()
                                .map(c -> new JobCategoryStats(
                                        c.getId(),
                                        c.getName(),
                                        (long) c.getJobs().size()
                                ))
                                .toList()
                )
                .build();
    }

    @Transactional(readOnly = true)
    public JobSeekerDashboardResponse getJobSeekerDashboard(Long jobSeekerId) {

        User currentUser = currentUser();

        if (currentUser.getRole() != UserRole.ADMIN &&
                !currentUser.getId().equals(jobSeekerId)) {
            throw new UnauthorizedException("Not authorized to view job seeker dashboard");
        }

        User jobSeekerUser = userRepository.findById(jobSeekerId)
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker not found"));

        JobSeekerProfile jobSeekerProfile = jobSeekerUser.getJobSeekerProfile();

        if (jobSeekerProfile == null) {
            throw new ResourceNotFoundException("Job seeker profile not found");
        }

        long totalApplications =
                applicationRepository.findByJobSeeker(jobSeekerProfile, Pageable.unpaged())
                        .getTotalElements();

        long applicationsLast30Days =
                applicationRepository.findByApplicationDateRange(
                        LocalDateTime.now().minusDays(30),
                        LocalDateTime.now(),
                        Pageable.unpaged()
                ).getTotalElements();

        Map<String, Long> statusBreakdown =
                Arrays.stream(ApplicationStatus.values())
                        .collect(Collectors.toMap(
                                ApplicationStatus::name,
                                status -> applicationRepository
                                        .countApplicationsByJobSeekerAndStatus(
                                                jobSeekerProfile, status)
                        ));

        List<JobSeekerDashboardResponse.RecentAppliedJob> recentApplications =
                applicationRepository
                        .findByJobSeeker(jobSeekerProfile, PageRequest.of(0, 5))
                        .stream()
                        .map(app -> new JobSeekerDashboardResponse.RecentAppliedJob(
                                app.getJob().getId(),
                                app.getJob().getTitle(),
                                app.getJob().getCompany().getName(),
                                app.getStatus(),
                                app.getAppliedAt()
                        ))
                        .toList();

        return JobSeekerDashboardResponse.builder()
                .jobSeekerId(jobSeekerId)
                .fullName(jobSeekerUser.getFullName())
                .totalApplications(totalApplications)
                .applicationsLast30Days(applicationsLast30Days)
                .applicationStatusBreakdown(statusBreakdown)
                .recentApplications(recentApplications)
                .build();
    }


}



