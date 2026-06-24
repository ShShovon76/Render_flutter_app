package com.example.job_portal_backend.dtos.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobCategoryStats {
    private Long categoryId;
    private String categoryName;
    private Long jobCount;
}
