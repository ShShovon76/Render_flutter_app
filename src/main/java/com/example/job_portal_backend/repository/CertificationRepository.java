package com.example.job_portal_backend.repository;

import com.example.job_portal_backend.entity.Certification;
import com.example.job_portal_backend.entity.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {
    List<Certification> findByJobSeekerProfile(JobSeekerProfile profile);
    void deleteByJobSeekerProfile(JobSeekerProfile profile);
}