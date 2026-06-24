package com.example.job_portal_backend.dtos.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer durationInDays;
    private List<String> features;
    private boolean active;
    private String stripePriceId;
    private Integer maxJobPosts;
    private boolean featuredJobs;
    private boolean companyVerification;
    private boolean analyticsAccess;
    private boolean prioritySupport;
}