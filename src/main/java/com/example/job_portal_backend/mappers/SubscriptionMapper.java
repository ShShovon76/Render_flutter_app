package com.example.job_portal_backend.mappers;

import com.example.job_portal_backend.dtos.system.SubscriptionDto;
import com.example.job_portal_backend.entity.Subscription;
import com.example.job_portal_backend.entity.SubscriptionPlan;
import com.example.job_portal_backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionMapper {

    private final SubscriptionPlanMapper planMapper;

    public SubscriptionDto toDto(Subscription subscription) {
        if (subscription == null) {
            return null;
        }

        return SubscriptionDto.builder()
                .id(subscription.getId())
                .userId(subscription.getUser() != null ? subscription.getUser().getId() : null)
                .plan(subscription.getPlan() != null ? planMapper.toDto(subscription.getPlan()) : null)
                .stripeSubscriptionId(subscription.getStripeSubscriptionId())
                .stripeCustomerId(subscription.getStripeCustomerId())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .active(subscription.isActive())
                .autoRenew(subscription.isAutoRenew())
                .createdAt(subscription.getCreatedAt())
                .cancelledAt(subscription.getCancelledAt())
                .build();
    }

    public Subscription toEntity(SubscriptionDto subscriptionDto) {
        if (subscriptionDto == null) {
            return null;
        }

        Subscription subscription = Subscription.builder()
                .id(subscriptionDto.getId())
                .stripeSubscriptionId(subscriptionDto.getStripeSubscriptionId())
                .stripeCustomerId(subscriptionDto.getStripeCustomerId())
                .startDate(subscriptionDto.getStartDate())
                .endDate(subscriptionDto.getEndDate())
                .active(subscriptionDto.isActive())
                .autoRenew(subscriptionDto.isAutoRenew())
                .createdAt(subscriptionDto.getCreatedAt())
                .cancelledAt(subscriptionDto.getCancelledAt())
                .build();

        if (subscriptionDto.getUserId() != null) {
            User user = new User();
            user.setId(subscriptionDto.getUserId());
            subscription.setUser(user);
        }

        if (subscriptionDto.getPlan() != null && subscriptionDto.getPlan().getId() != null) {
            SubscriptionPlan plan = new SubscriptionPlan();
            plan.setId(subscriptionDto.getPlan().getId());
            subscription.setPlan(plan);
        }

        return subscription;
    }
}
