package com.example.job_portal_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "system_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean maintenanceMode = false;

    @Column(nullable = false)
    private boolean registrationEnabled = true;

    @Column(nullable = false)
    private String siteName = "Job Portal";

    private String siteLogoUrl;

    private String siteFaviconUrl;

    private String contactEmail;

    private String contactPhone;

    private String address;

    @Column(length = 2000)
    private String privacyPolicy;

    @Column(length = 2000)
    private String termsAndConditions;
}
