package com.example.job_portal_backend.controller;

import com.example.job_portal_backend.dtos.system.SubscriptionDto;
import com.example.job_portal_backend.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<SubscriptionDto> getUserSubscription(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getUserSubscription(userId));
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<SubscriptionDto> getActiveSubscription(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getActiveSubscription(userId));
    }

    @GetMapping("/user/{userId}/has-active")
    public ResponseEntity<Boolean> hasActiveSubscription(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.hasActiveSubscription(userId));
    }

    @PostMapping
    public ResponseEntity<SubscriptionDto> createSubscription(
            @RequestParam Long userId,
            @RequestParam Long planId,
            @RequestParam String stripeSubscriptionId,
            @RequestParam String stripeCustomerId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subscriptionService.createSubscription(userId, planId, stripeSubscriptionId, stripeCustomerId));
    }

    @PutMapping("/{subscriptionId}/cancel")
    public ResponseEntity<SubscriptionDto> cancelSubscription(
            @PathVariable Long subscriptionId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(subscriptionService.cancelSubscription(subscriptionId, userId));
    }

    @PutMapping("/{subscriptionId}/renew")
    public ResponseEntity<SubscriptionDto> renewSubscription(@PathVariable Long subscriptionId) {
        return ResponseEntity.ok(subscriptionService.renewSubscription(subscriptionId));
    }

    @PutMapping("/status")
    public ResponseEntity<Void> updateSubscriptionStatus(
            @RequestParam String stripeSubscriptionId,
            @RequestParam boolean active) {
        subscriptionService.updateSubscriptionStatus(stripeSubscriptionId, active);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<SubscriptionDto>> getExpiringSubscriptions(
            @RequestParam(defaultValue = "7") int daysBeforeExpiry) {
        return ResponseEntity.ok(subscriptionService.getExpiringSubscriptions(daysBeforeExpiry));
    }
}