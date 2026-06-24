package com.example.job_portal_backend.repository;

import com.example.job_portal_backend.dtos.analytics.DateCountProjection;
import com.example.job_portal_backend.dtos.analytics.StatusCountProjection;
import com.example.job_portal_backend.entity.*;
import com.example.job_portal_backend.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    Optional<JobApplication> findByJobAndJobSeeker(Job job, JobSeekerProfile jobSeeker);
    boolean existsByJobSeeker_User_IdAndJob_Employer_Id(
            Long userId,
            Long employerId
    );

    Page<JobApplication> findByJob(Job job, Pageable pageable);

    Page<JobApplication> findByJobSeeker(JobSeekerProfile jobSeeker, Pageable pageable);

    Page<JobApplication> findByStatus(ApplicationStatus status, Pageable pageable);

    @Query("SELECT ja FROM JobApplication ja WHERE ja.job.employer = :employer")
    Page<JobApplication> findByEmployer(@Param("employer") User employer, Pageable pageable);

    boolean existsByResume(Resume resume);
    boolean existsByResumeIdAndJob_Employer_Id(Long resumeId, Long employerId);

    @Query("SELECT COUNT(ja) FROM JobApplication ja WHERE ja.jobSeeker = :jobSeeker AND ja.status = :status")
    long countApplicationsByJobSeekerAndStatus(
            @Param("jobSeeker") JobSeekerProfile jobSeeker,
            @Param("status") ApplicationStatus status
    );

    boolean existsByJobAndJobSeeker(Job job, JobSeekerProfile jobSeeker);


    @Query("""
SELECT h FROM ApplicationStatusHistory h
WHERE h.application.id = :applicationId
ORDER BY h.changedAt DESC
""")
    List<ApplicationStatusHistory> findHistoryByApplicationId(
            @Param("applicationId") Long applicationId
    );


    @Query("SELECT ja FROM JobApplication ja WHERE " +
            "ja.jobSeeker = :jobSeeker AND " +
            "(:status IS NULL OR ja.status = :status)")
    Page<JobApplication> findByJobSeekerWithStatus(
            @Param("jobSeeker") JobSeekerProfile jobSeeker,
            @Param("status") ApplicationStatus status,
            Pageable pageable
    );

    @Query("SELECT ja FROM JobApplication ja WHERE " +
            "ja.appliedAt >= :startDate AND " +
            "ja.appliedAt <= :endDate")
    Page<JobApplication> findByApplicationDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );



    @Query("SELECT COUNT(ja) FROM JobApplication ja WHERE ja.job.employer = :employer AND ja.appliedAt > :date")
    Long countByEmployerAndAppliedAtAfter(@Param("employer") User employer,
                                          @Param("date") LocalDateTime date);

   /* =====================================================
       Daily application counts (PROJECTION)
       ===================================================== */

    @Query("""
    SELECT new com.example.job_portal_backend.dtos.analytics.DateCountProjection(
        CAST(ja.appliedAt AS LocalDate),
        COUNT(ja)
    )
    FROM JobApplication ja
    WHERE (:jobId IS NULL OR ja.job.id = :jobId)
      AND (:employerId IS NULL OR ja.job.employer.id = :employerId)
      AND ja.appliedAt BETWEEN :from AND :to
    GROUP BY CAST(ja.appliedAt AS LocalDate)
    """)
    List<DateCountProjection> getDailyApplicationCounts(
            @Param("jobId") Long jobId,
            @Param("employerId") Long employerId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );




/* =====================================================
       Status breakdown (PROJECTION)
       ===================================================== */

    @Query("""
    SELECT new com.example.job_portal_backend.dtos.analytics.StatusCountProjection(
        ja.status,
        COUNT(ja)
    )
    FROM JobApplication ja
    WHERE (:jobId IS NULL OR ja.job.id = :jobId)
      AND (:employerId IS NULL OR ja.job.employer.id = :employerId)
    GROUP BY ja.status
    """)
    List<StatusCountProjection> getApplicationStatusBreakdown(
            @Param("jobId") Long jobId,
            @Param("employerId") Long employerId
    );


    @Query("SELECT COUNT(ja) FROM JobApplication ja WHERE ja.job.employer = :employer")
    long countByEmployer(@Param("employer") User employer);
    @Query("SELECT COUNT(ja) FROM JobApplication ja WHERE ja.job = :job")
    long countApplicationsByJob(@Param("job") Job job);
    // NEW: Count by applied date after
    Long countByAppliedAtAfter(LocalDateTime date);

    @Query("""
SELECT a FROM JobApplication a
WHERE a.job.employer.id = :employerId
ORDER BY a.appliedAt DESC
""")
    Page<JobApplication> findRecentByEmployer(Long employerId, Pageable pageable);

    boolean existsByJobIdAndJobSeeker_User_Id(Long jobId, Long userId);

    @Query("""
SELECT COUNT(a) > 0
FROM JobApplication a
WHERE a.job.id = :jobId
  AND a.jobSeeker.user.id = :userId
  AND a.status <> 'CANCELLED'
""")
    boolean hasActiveApplication(Long jobId, Long userId);
    @Query("""
SELECT COUNT(a) > 0
FROM JobApplication a
WHERE a.resume.id = :resumeId
  AND a.job.employer.id = :employerId
""")
    boolean employerCanAccessResume(
            @Param("resumeId") Long resumeId,
            @Param("employerId") Long employerId
    );

    Page<JobApplication> findByJob_Employer_IdOrderByAppliedAtDesc(
            Long employerId,
            Pageable pageable
    );

}