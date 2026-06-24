package com.example.job_portal_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "companies", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User owner;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String industry;

    private String companySize;
    private String logoUrl;
    private String coverImageUrl;

    @Column(length = 4000)
    private String about;

    private String website;
    private String email;
    private String phone;
    private String address;
    private Integer foundedYear;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SocialLink> socialLinks = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Job> jobs = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CompanyReview> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SavedCompany> savedByJobSeekers = new ArrayList<>();

    @Column(nullable = false)
    private boolean verified = false;

    @Column(precision = 2, scale = 1)
    private BigDecimal rating;

    private Integer reviewCount = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        calculateRating();
    }

    @PreUpdate
    public void onUpdate() {
        calculateRating();
    }

    private void calculateRating() {
        if (reviews != null && !reviews.isEmpty()) {
            double avg = reviews.stream()
                    .mapToInt(CompanyReview::getRating)
                    .average()
                    .orElse(0.0);

            this.rating = BigDecimal.valueOf(avg)
                    .setScale(1, RoundingMode.HALF_UP);

            this.reviewCount = reviews.size();
        }
    }

    // Only use ID for equality and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Company other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
