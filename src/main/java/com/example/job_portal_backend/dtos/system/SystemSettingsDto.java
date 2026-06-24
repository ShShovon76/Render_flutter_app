package com.example.job_portal_backend.dtos.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettingsDto {
    private boolean maintenanceMode;
    private boolean registrationEnabled;
    private String siteName;
    private String siteLogoUrl;
    private String siteFaviconUrl;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String privacyPolicy;
    private String termsAndConditions;
}