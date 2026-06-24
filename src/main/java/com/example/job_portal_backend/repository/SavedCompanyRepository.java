package com.example.job_portal_backend.repository;

import com.example.job_portal_backend.entity.Company;
import com.example.job_portal_backend.entity.JobSeekerProfile;
import com.example.job_portal_backend.entity.SavedCompany;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface SavedCompanyRepository extends JpaRepository<SavedCompany, Long> {

    Optional<SavedCompany> findByCompanyAndJobSeeker(Company company, JobSeekerProfile jobSeeker);

    Page<SavedCompany> findByJobSeeker(JobSeekerProfile jobSeeker, Pageable pageable);

    boolean existsByCompanyAndJobSeeker(Company company, JobSeekerProfile jobSeeker);

    @Query("SELECT COUNT(sc) FROM SavedCompany sc WHERE sc.company = :company")
    long countByCompany(@Param("company") Company company);

    @Modifying
    @Transactional
    void deleteByCompanyAndJobSeeker(Company company, JobSeekerProfile jobSeeker);
}
