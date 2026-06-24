package com.example.job_portal_backend.repository;

import com.example.job_portal_backend.entity.Company;
import com.example.job_portal_backend.entity.User;
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
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByName(String name);

    List<Company> findByOwner(User owner);

    Page<Company> findByOwner(User owner, Pageable pageable);

    Page<Company> findByVerified(boolean verified, Pageable pageable);

    @Query("SELECT c FROM Company c WHERE " +
            "(:industry IS NULL OR c.industry = :industry) AND " +
            "(:verified IS NULL OR c.verified = :verified) AND " +
            "(:keyword IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.about) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Company> findCompaniesWithFilters(
            @Param("industry") String industry,
            @Param("verified") Boolean verified,
            @Param("keyword") String keyword,
            Pageable pageable  // Pageable already contains sorting info - NO NEED for @Param("sort")
    );

    @Query("SELECT c FROM Company c ORDER BY c.rating DESC NULLS LAST")
    Page<Company> findTopRatedCompanies(Pageable pageable);

    @Query("SELECT c FROM Company c WHERE c.rating >= :minRating")
    Page<Company> findByMinRating(@Param("minRating") Double minRating, Pageable pageable);
    Long countByVerified(boolean verified);

    @Query("SELECT COUNT(c) FROM Company c WHERE c.createdAt > :date")
    Long countByCreatedAtAfter(@Param("date") LocalDateTime date);
}
