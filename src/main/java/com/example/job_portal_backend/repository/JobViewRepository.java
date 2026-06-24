package com.example.job_portal_backend.repository;


import com.example.job_portal_backend.dtos.analytics.DateCountProjection;
import com.example.job_portal_backend.entity.Job;
import com.example.job_portal_backend.entity.JobView;
import com.example.job_portal_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface JobViewRepository extends JpaRepository<JobView, Long> {
    @Query("SELECT COUNT(v) FROM JobView v WHERE v.job = :job AND v.viewDate >= :dateAfter")
    long countByJobAndViewDateAfter(@Param("job") Job job, @Param("dateAfter") LocalDateTime dateAfter);

    boolean existsByJobAndViewerAndViewDateAfter(Job job, User viewer, LocalDateTime dateAfter);
    @Query("SELECT COUNT(jv) FROM JobView jv WHERE jv.job = :job AND jv.viewDate BETWEEN :fromDate AND :toDate")
    Long countByJobAndViewDateBetween(@Param("job") Job job,
                                      @Param("fromDate") LocalDateTime fromDate,
                                      @Param("toDate") LocalDateTime toDate);

    @Query("SELECT COUNT(DISTINCT jv.viewer) FROM JobView jv WHERE jv.job.id = :jobId AND jv.viewDate BETWEEN :fromDate AND :toDate")
    Long countDistinctViewersByJobAndDateRange(@Param("jobId") Long jobId,
                                               @Param("fromDate") LocalDateTime fromDate,
                                               @Param("toDate") LocalDateTime toDate);

    @Query("""
SELECT new com.example.job_portal_backend.dtos.analytics.DateCountProjection(
    CAST(jv.viewDate AS LocalDate),
    COUNT(jv)
)
FROM JobView jv
WHERE jv.job.id = :jobId
AND jv.viewDate BETWEEN :fromDate AND :toDate
GROUP BY CAST(jv.viewDate AS LocalDate)
""")
    List<DateCountProjection> getDailyViewCounts(
            @Param("jobId") Long jobId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

}
