package com.example.job_portal_backend.entity;

import com.example.job_portal_backend.enums.SocialLinkType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "social_links")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialLinkType type;

    @Column(nullable = false)
    private String url;
}
