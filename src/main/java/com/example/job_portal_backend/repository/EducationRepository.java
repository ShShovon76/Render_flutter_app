package com.example.job_portal_backend.repository;

import com.example.job_portal_backend.entity.Education;
import com.example.job_portal_backend.entity.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findByJobSeekerProfile(JobSeekerProfile profile);
    void deleteByJobSeekerProfile(JobSeekerProfile profile);
}