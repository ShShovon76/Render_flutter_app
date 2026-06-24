package com.example.job_portal_backend.service;

import com.example.job_portal_backend.dtos.system.SubscriptionPlanDto;
import com.example.job_portal_backend.entity.SubscriptionPlan;
import com.example.job_portal_backend.exceptions.ResourceNotFoundException;
import com.example.job_portal_backend.mappers.SubscriptionPlanMapper;
import com.example.job_portal_backend.repository.SubscriptionPlanRepository;
import com.example.job_portal_backend.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionPlanService {

    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionPlanMapper planMapper;
    private final SubscriptionRepository subscriptionRepository;

    public List<SubscriptionPlanDto> getAllPlans() {
        return planRepository.findAll()
                .stream()
                .map(planMapper::toDto)
                .toList();
    }

    public List<SubscriptionPlanDto> getActivePlans() {
        return planRepository.findByActiveTrue()
                .stream()
                .map(planMapper::toDto)
                .toList();
    }

    public Page<SubscriptionPlanDto> getPlans(Pageable pageable) {
        return planRepository.findAll(pageable)
                .map(planMapper::toDto);
    }

    public SubscriptionPlanDto getPlanById(Long id) {
        SubscriptionPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + id));
        return planMapper.toDto(plan);
    }

    public SubscriptionPlan getPlanEntity(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + id));
    }

    @Transactional
    public SubscriptionPlanDto createPlan(SubscriptionPlanDto planDto) {
        // Check if plan name already exists
        if (planRepository.existsByName(planDto.getName())) {
            throw new IllegalArgumentException("Plan name already exists: " + planDto.getName());
        }

        SubscriptionPlan plan = planMapper.toEntity(planDto);
        SubscriptionPlan savedPlan = planRepository.save(plan);

        log.info("Subscription plan created: {}", savedPlan.getName());
        return planMapper.toDto(savedPlan);
    }

    @Transactional
    public SubscriptionPlanDto updatePlan(Long id, SubscriptionPlanDto planDto) {
        SubscriptionPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + id));

        // Check name uniqueness if changing name
        if (planDto.getName() != null && !planDto.getName().equals(plan.getName())) {
            if (planRepository.existsByName(planDto.getName())) {
                throw new IllegalArgumentException("Plan name already exists: " + planDto.getName());
            }
            plan.setName(planDto.getName());
        }

        if (planDto.getDescription() != null) plan.setDescription(planDto.getDescription());
        if (planDto.getPrice() != null) plan.setPrice(planDto.getPrice());
        if (planDto.getDurationInDays() != null) plan.setDurationInDays(planDto.getDurationInDays());
        if (planDto.getFeatures() != null) plan.setFeatures(planDto.getFeatures());
        if (planDto.getStripePriceId() != null) plan.setStripePriceId(planDto.getStripePriceId());
        if (planDto.getMaxJobPosts() != null) plan.setMaxJobPosts(planDto.getMaxJobPosts());
        if (planDto.isFeaturedJobs() != plan.isFeaturedJobs()) plan.setFeaturedJobs(planDto.isFeaturedJobs());
        if (planDto.isCompanyVerification() != plan.isCompanyVerification()) plan.setCompanyVerification(planDto.isCompanyVerification());
        if (planDto.isAnalyticsAccess() != plan.isAnalyticsAccess()) plan.setAnalyticsAccess(planDto.isAnalyticsAccess());
        if (planDto.isPrioritySupport() != plan.isPrioritySupport()) plan.setPrioritySupport(planDto.isPrioritySupport());

        SubscriptionPlan updatedPlan = planRepository.save(plan);
        return planMapper.toDto(updatedPlan);
    }

    @Transactional
    public void activatePlan(Long id, boolean active) {
        SubscriptionPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + id));

        plan.setActive(active);
        planRepository.save(plan);

        log.info("Plan {} activated status changed to: {}", plan.getName(), active);
    }

    @Transactional
    public void deletePlan(Long id) {
        SubscriptionPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + id));

        if (subscriptionRepository.existsByPlan_Id(id)) {
            throw new IllegalStateException("Cannot delete plan with active subscriptions");
        }

        planRepository.delete(plan);
        log.info("Plan deleted: {}", id);
    }
}
