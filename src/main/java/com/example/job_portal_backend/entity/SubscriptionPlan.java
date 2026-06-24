package com.example.job_portal_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subscription_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer durationInDays;

    @ElementCollection
    @CollectionTable(name = "subscription_plan_features", joinColumns = @JoinColumn(name = "plan_id"))
    @Column(name = "feature")
    private List<String> features = new ArrayList<>();

    @Column(nullable = false)
    private String stripePriceId;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private Integer maxJobPosts = 0;

    @Column(nullable = false)
    private boolean featuredJobs = false;

    @Column(nullable = false)
    private boolean companyVerification = false;

    @Column(nullable = false)
    private boolean analyticsAccess = false;

    @Column(nullable = false)
    private boolean prioritySupport = false;
}
