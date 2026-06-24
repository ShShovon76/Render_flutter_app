package com.example.job_portal_backend.repository;

import com.example.job_portal_backend.entity.Job;
import com.example.job_portal_backend.entity.JobSeekerProfile;
import com.example.job_portal_backend.entity.SavedJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    Optional<SavedJob> findByJobAndJobSeeker(Job job, JobSeekerProfile jobSeeker);

    @EntityGraph(attributePaths = {"job", "job.company"})
    Page<SavedJob> findByJobSeeker(JobSeekerProfile jobSeeker, Pageable pageable);

    boolean existsByJobAndJobSeeker(Job job, JobSeekerProfile jobSeeker);

    @Query("SELECT COUNT(sj) FROM SavedJob sj WHERE sj.job = :job")
    long countByJob(@Param("job") Job job);

    @Query("SELECT sj FROM SavedJob sj WHERE sj.jobSeeker = :jobSeeker ORDER BY sj.savedAt DESC")
    Page<SavedJob> findRecentSavedJobs(@Param("jobSeeker") JobSeekerProfile jobSeeker, Pageable pageable);

    @Modifying
    @Transactional
    void deleteByJobAndJobSeeker(Job job, JobSeekerProfile jobSeeker);
}
