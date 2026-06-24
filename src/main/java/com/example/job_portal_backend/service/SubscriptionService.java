package com.example.job_portal_backend.service;

import com.example.job_portal_backend.dtos.system.SubscriptionDto;
import com.example.job_portal_backend.entity.Subscription;
import com.example.job_portal_backend.entity.SubscriptionPlan;
import com.example.job_portal_backend.entity.User;
import com.example.job_portal_backend.exceptions.ResourceNotFoundException;
import com.example.job_portal_backend.mappers.SubscriptionMapper;
import com.example.job_portal_backend.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanService planService;
    private final UserService userService;
    private final SubscriptionMapper subscriptionMapper;

    public SubscriptionDto getUserSubscription(Long userId) {
        User user = userService.getUserEntity(userId);

        Subscription subscription = subscriptionRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found for user: " + userId));

        return subscriptionMapper.toDto(subscription);
    }

    public SubscriptionDto getActiveSubscription(Long userId) {
        User user = userService.getUserEntity(userId);

        Subscription subscription = subscriptionRepository.findActiveSubscriptionByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Active subscription not found for user: " + userId));

        return subscriptionMapper.toDto(subscription);
    }

    public boolean hasActiveSubscription(Long userId) {
        User user = userService.getUserEntity(userId);
        return subscriptionRepository.findActiveSubscriptionByUser(user).isPresent();
    }

    @Transactional
    public SubscriptionDto createSubscription(Long userId, Long planId, String stripeSubscriptionId, String stripeCustomerId) {
        User user = userService.getUserEntity(userId);
        SubscriptionPlan plan = planService.getPlanEntity(planId);

        // Check if user already has active subscription
        subscriptionRepository.findByUser(user).ifPresent(sub -> {
            if (sub.isActive()) {
                throw new IllegalStateException("User already has an active subscription");
            }
        });

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .stripeSubscriptionId(stripeSubscriptionId)
                .stripeCustomerId(stripeCustomerId)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(plan.getDurationInDays()))
                .active(true)
                .autoRenew(true)
                .build();

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        log.info("Subscription created for user: {} with plan: {}", userId, plan.getName());

        return subscriptionMapper.toDto(savedSubscription);
    }

    @Transactional
    public SubscriptionDto cancelSubscription(Long subscriptionId, Long userId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + subscriptionId));

        // Verify ownership
        if (!subscription.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Subscription not found for user");
        }

        subscription.setActive(false);
        subscription.setAutoRenew(false);
        subscription.setCancelledAt(LocalDateTime.now());

        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        log.info("Subscription cancelled: {} by user: {}", subscriptionId, userId);

        return subscriptionMapper.toDto(updatedSubscription);
    }

    @Transactional
    public SubscriptionDto renewSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + subscriptionId));

        // Check if subscription can be renewed
        if (subscription.isActive() && subscription.isAutoRenew()) {
            subscription.setEndDate(subscription.getEndDate().plusDays(subscription.getPlan().getDurationInDays()));
            Subscription updated = subscriptionRepository.save(subscription);
            log.info("Subscription renewed: {}", subscriptionId);
            return subscriptionMapper.toDto(updated);
        }

        throw new IllegalStateException("Subscription cannot be renewed");
    }

    @Transactional
    public void updateSubscriptionStatus(String stripeSubscriptionId, boolean active) {
        Subscription subscription = subscriptionRepository.findByStripeSubscriptionId(stripeSubscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with Stripe ID: " + stripeSubscriptionId));

        subscription.setActive(active);
        if (!active) {
            subscription.setCancelledAt(LocalDateTime.now());
        }

        subscriptionRepository.save(subscription);
        log.info("Subscription status updated: {} -> active: {}", stripeSubscriptionId, active);
    }

    public List<SubscriptionDto> getExpiringSubscriptions(int daysBeforeExpiry) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(daysBeforeExpiry);

        return subscriptionRepository.findSubscriptionsExpiringBetween(startDate, endDate)
                .stream()
                .map(subscriptionMapper::toDto)
                .toList();
    }

    @Transactional
    public void deactivateExpiredSubscriptions() {
        List<Subscription> expiredSubscriptions = subscriptionRepository.findByActiveTrueAndEndDateBefore(LocalDateTime.now());

        for (Subscription subscription : expiredSubscriptions) {
            subscription.setActive(false);
            log.info("Subscription deactivated due to expiry: {}", subscription.getId());
        }

        subscriptionRepository.saveAll(expiredSubscriptions);
    }
}
