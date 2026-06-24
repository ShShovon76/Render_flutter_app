package com.example.job_portal_backend.dtos.analytics;

import com.example.job_portal_backend.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerDashboardResponse {

    private Long jobSeekerId;
    private String fullName;

    private long totalApplications;
    private long applicationsLast30Days;

    private Map<String, Long> applicationStatusBreakdown;

    private List<RecentAppliedJob> recentApplications;

    @Data
    @AllArgsConstructor
    public static class RecentAppliedJob {
        private Long jobId;
        private String jobTitle;
        private String companyName;
        private ApplicationStatus status;
        private LocalDateTime appliedAt;
    }
}

