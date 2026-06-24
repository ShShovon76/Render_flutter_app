package com.example.job_portal_backend.repository;

import com.example.job_portal_backend.entity.Company;
import com.example.job_portal_backend.entity.Job;
import com.example.job_portal_backend.entity.User;
import com.example.job_portal_backend.enums.ExperienceLevel;
import com.example.job_portal_backend.enums.JobStatus;
import com.example.job_portal_backend.enums.JobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    Page<Job> findByEmployer(User employer, Pageable pageable);


    Page<Job> findByStatus(JobStatus status, Pageable pageable);

    List<Job> findByDeadlineBeforeAndStatus(LocalDate deadline, JobStatus status);


    @Query("SELECT j FROM Job j WHERE " +
            "(:minSalary IS NULL OR j.minSalary >= :minSalary) AND " +
            "(:maxSalary IS NULL OR j.maxSalary <= :maxSalary) AND " +
            "j.status = 'ACTIVE'")
    Page<Job> findBySalaryRange(
            @Param("minSalary") Double minSalary,
            @Param("maxSalary") Double maxSalary,
            Pageable pageable
    );

    @Query("SELECT j FROM Job j WHERE " +
            "EXISTS (SELECT 1 FROM j.skills s WHERE s IN :skills) AND " +
            "j.status = 'ACTIVE'")
    Page<Job> findBySkills(@Param("skills") List<String> skills, Pageable pageable);

    @Query("SELECT j FROM Job j WHERE j.postedAt >= :dateFrom AND j.status = 'ACTIVE'")
    Page<Job> findRecentJobs(@Param("dateFrom") LocalDateTime dateFrom, Pageable pageable);

    @Query("SELECT COUNT(j) FROM Job j WHERE j.employer = :employer AND j.status = 'ACTIVE'")
    long countActiveJobsByEmployer(@Param("employer") User employer);
    @Query("SELECT COUNT(j) FROM Job j WHERE j.employer = :employer AND j.status = :status")
    Long countByEmployerAndStatus(@Param("employer") User employer,
                                  @Param("status") JobStatus status);

    @Query("SELECT j FROM Job j WHERE j.employer = :employer ORDER BY j.viewsCount DESC")
    Page<Job> findTopViewedJobsByEmployer(@Param("employer") User employer, Pageable pageable);

    Long countByPostedAtAfter(LocalDateTime date);

    Long countByEmployer(User employer);

    Long countByStatus(JobStatus status);
    @Query("SELECT COUNT(DISTINCT jv.viewer.id) FROM JobView jv WHERE jv.job = :job")
    int countDistinctViewersByJob(@Param("job") Job job);

    @Query("select max(jv.viewDate) from JobView jv where jv.job = :job")
    LocalDateTime findLastViewDate(@Param("job") Job job);
    @EntityGraph(attributePaths = {
            "company",
            "category",
            "employer"
    })
    Page<Job> findAll(Pageable pageable);
    @EntityGraph(attributePaths = {
            "company",
            "category",
            "employer"
    })
    Page<Job> findByCompany(Company company, Pageable pageable);
    @EntityGraph(attributePaths = {
            "company",
            "category",
            "employer"
    })
    @Query("""
SELECT j FROM Job j
WHERE (:keyword IS NULL
        OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
  AND (:location IS NULL
        OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
  AND (:categoryId IS NULL OR j.category.id = :categoryId)
  AND (:jobType IS NULL OR j.jobType = :jobType)
  AND (:experienceLevel IS NULL OR j.experienceLevel = :experienceLevel)
  AND (:remote IS NULL OR j.remoteAllowed = :remote)
  AND (:minSalary IS NULL OR j.maxSalary >= :minSalary)
  AND (:maxSalary IS NULL OR j.minSalary <= :maxSalary)
  AND j.status = 'ACTIVE'
""")
    Page<Job> searchJobs(
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("categoryId") Long categoryId,
            @Param("jobType") JobType jobType,
            @Param("experienceLevel") ExperienceLevel experienceLevel,
            @Param("remote") Boolean remote,
            @Param("minSalary") Double minSalary,
            @Param("maxSalary") Double maxSalary,
            Pageable pageable
    );


    @EntityGraph(attributePaths = {
            "company",
            "category",
            "employer"
    })
    @Query("""
    SELECT j FROM Job j
    WHERE j.employer = :employer
      AND (:status IS NULL OR j.status = :status)
      AND (:jobType IS NULL OR j.jobType = :jobType)
      AND (
            :keyword IS NULL OR
            LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
""")
    Page<Job> searchEmployerJobs(
            @Param("employer") User employer,
            @Param("status") JobStatus status,
            @Param("jobType") JobType jobType,
            @Param("keyword") String keyword,
            Pageable pageable
    );

}
