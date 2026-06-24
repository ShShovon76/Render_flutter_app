package com.example.job_portal_backend.repository;

import com.example.job_portal_backend.entity.JobSeekerProfile;
import com.example.job_portal_backend.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    List<Resume> findByJobSeeker(JobSeekerProfile jobSeeker);

    Optional<Resume> findByIdAndJobSeeker(Long id, JobSeekerProfile jobSeeker);

    Optional<Resume> findByJobSeekerAndPrimaryResumeTrue(JobSeekerProfile jobSeeker);
}



