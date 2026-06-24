package com.example.job_portal_backend.repository;

import com.example.job_portal_backend.entity.JobCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobCategoryRepository extends JpaRepository<JobCategory, Long> {
    Optional<JobCategory> findByName(String name);
    boolean existsByName(String name);
    @Query("SELECT c FROM JobCategory c ORDER BY SIZE(c.jobs) DESC")
    Page<JobCategory> findTopCategoriesByJobCount(Pageable pageable);
    @Query("SELECT DISTINCT c FROM JobCategory c LEFT JOIN FETCH c.jobs")
    List<JobCategory> findAllWithCounts();

}
