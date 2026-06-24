package com.example.job_portal_backend.repository;


import com.example.job_portal_backend.entity.Company;
import com.example.job_portal_backend.entity.CompanyReview;
import com.example.job_portal_backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyReviewRepository extends JpaRepository<CompanyReview, Long> {
    Page<CompanyReview> findByCompany(Company company, Pageable pageable);
    Optional<CompanyReview> findByCompanyAndReviewer(Company company, User reviewer);
    long countByCompany(Company company);
}