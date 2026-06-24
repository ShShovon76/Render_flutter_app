package com.example.job_portal_backend.dtos.searchAndPaginaton;

import com.example.job_portal_backend.enums.ExperienceLevel;
import com.example.job_portal_backend.enums.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSearchFilter {
    private String keyword;
    private String location;
    private Long categoryId;
    private JobType jobType;
    private SalaryRange salaryRange;
    private ExperienceLevel experienceLevel;
    private Boolean remote;
    private Integer page = 0;
    private Integer size = 10;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalaryRange {
        private Double min;
        private Double max;
    }
}
