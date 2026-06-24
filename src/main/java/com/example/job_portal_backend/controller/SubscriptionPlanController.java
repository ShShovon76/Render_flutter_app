package com.example.job_portal_backend.controller;

import com.example.job_portal_backend.dtos.system.SubscriptionPlanDto;
import com.example.job_portal_backend.service.SubscriptionPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscription-plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionPlanService planService;

    @GetMapping("/all")
    public ResponseEntity<List<SubscriptionPlanDto>> getAllPlans() {
        return ResponseEntity.ok(planService.getAllPlans());
    }

    @GetMapping("/active")
    public ResponseEntity<List<SubscriptionPlanDto>> getActivePlans() {
        return ResponseEntity.ok(planService.getActivePlans());
    }

    @GetMapping
    public ResponseEntity<Page<SubscriptionPlanDto>> getPlans(Pageable pageable) {
        return ResponseEntity.ok(planService.getPlans(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionPlanDto> getPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(planService.getPlanById(id));
    }

    @PostMapping
    public ResponseEntity<SubscriptionPlanDto> createPlan(@Valid @RequestBody SubscriptionPlanDto planDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(planService.createPlan(planDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionPlanDto> updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody SubscriptionPlanDto planDto) {
        return ResponseEntity.ok(planService.updatePlan(id, planDto));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activatePlan(
            @PathVariable Long id,
            @RequestParam boolean active) {
        planService.activatePlan(id, active);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        planService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }
}