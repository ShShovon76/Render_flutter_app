package com.example.job_portal_backend.repository;

import com.example.job_portal_backend.entity.JobSeekerProfile;
import com.example.job_portal_backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobSeekerProfileRepository extends JpaRepository<JobSeekerProfile, Long> {

    Optional<JobSeekerProfile> findByUser(User user);
    Optional<JobSeekerProfile> findById(Long id);
    Optional<JobSeekerProfile> findByUserId(Long userId);

    @Query("SELECT p FROM JobSeekerProfile p WHERE " +
            "(:keyword IS NULL OR " +
            "LOWER(p.headline) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "EXISTS (SELECT 1 FROM p.skills s WHERE LOWER(s) LIKE LOWER(CONCAT('%', :keyword, '%'))))")
    Page<JobSeekerProfile> searchProfiles(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM JobSeekerProfile p JOIN p.user u WHERE " +
            "(:skill IS NULL OR :skill MEMBER OF p.skills) AND " +
            "(:location IS NULL OR :location MEMBER OF p.preferredLocations)")
    Page<JobSeekerProfile> findProfilesBySkillAndLocation(
            @Param("skill") String skill,
            @Param("location") String location,
            Pageable pageable
    );
}