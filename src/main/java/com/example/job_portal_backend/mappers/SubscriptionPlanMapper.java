package com.example.job_portal_backend.mappers;

import com.example.job_portal_backend.dtos.system.SubscriptionPlanDto;
import com.example.job_portal_backend.entity.SubscriptionPlan;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SubscriptionPlanMapper {

    public SubscriptionPlanDto toDto(SubscriptionPlan plan) {
        if (plan == null) {
            return null;
        }

        return SubscriptionPlanDto.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .price(plan.getPrice() != null ? plan.getPrice() : BigDecimal.ZERO)
                .durationInDays(plan.getDurationInDays())
                .features(plan.getFeatures())
                .active(plan.isActive())
                .maxJobPosts(plan.getMaxJobPosts())
                .stripePriceId(plan.getStripePriceId())
                .featuredJobs(plan.isFeaturedJobs())
                .companyVerification(plan.isCompanyVerification())
                .analyticsAccess(plan.isAnalyticsAccess())
                .prioritySupport(plan.isPrioritySupport())
                .build();
    }

    public SubscriptionPlan toEntity(SubscriptionPlanDto planDto) {
        if (planDto == null) {
            return null;
        }

        return SubscriptionPlan.builder()
                .id(planDto.getId())
                .name(planDto.getName())
                .description(planDto.getDescription())
                .price(planDto.getPrice() != null ? planDto.getPrice() : BigDecimal.ZERO)
                .durationInDays(planDto.getDurationInDays())
                .features(planDto.getFeatures())
                .active(planDto.isActive())
                .maxJobPosts(planDto.getMaxJobPosts() != null ? planDto.getMaxJobPosts() : 0)
                .featuredJobs(planDto.isFeaturedJobs())
                .stripePriceId(planDto.getStripePriceId())
                .companyVerification(planDto.isCompanyVerification())
                .analyticsAccess(planDto.isAnalyticsAccess())
                .prioritySupport(planDto.isPrioritySupport())
                .build();
    }
}
