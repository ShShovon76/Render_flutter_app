package com.example.job_portal_backend.repository;

import com.example.job_portal_backend.entity.Subscription;
import com.example.job_portal_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByUser(User user);

    Optional<Subscription> findByStripeSubscriptionId(String stripeSubscriptionId);

    List<Subscription> findByActiveTrueAndEndDateBefore(LocalDateTime endDate);

    List<Subscription> findByAutoRenewTrueAndActiveTrue();

    @Query("SELECT s FROM Subscription s WHERE s.user = :user AND s.active = true")
    Optional<Subscription> findActiveSubscriptionByUser(@Param("user") User user);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.active = true")
    long countActiveSubscriptions();

    @Query("SELECT s FROM Subscription s WHERE s.endDate BETWEEN :startDate AND :endDate")
    List<Subscription> findSubscriptionsExpiringBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    boolean existsByPlan_Id(Long planId);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.createdAt > :date")
    Long countByCreatedAtAfter(@Param("date") LocalDateTime date);
}