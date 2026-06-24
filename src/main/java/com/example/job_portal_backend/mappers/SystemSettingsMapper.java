package com.example.job_portal_backend.mappers;

import com.example.job_portal_backend.dtos.system.SystemSettingsDto;
import com.example.job_portal_backend.entity.SystemSettings;
import org.springframework.stereotype.Component;

@Component
public class SystemSettingsMapper {

    public SystemSettingsDto toDto(SystemSettings settings) {
        if (settings == null) {
            return null;
        }

        return SystemSettingsDto.builder()
                .maintenanceMode(settings.isMaintenanceMode())
                .registrationEnabled(settings.isRegistrationEnabled())
                .siteName(settings.getSiteName())
                .siteLogoUrl(settings.getSiteLogoUrl())
                .siteFaviconUrl(settings.getSiteFaviconUrl())
                .contactEmail(settings.getContactEmail())
                .contactPhone(settings.getContactPhone())
                .address(settings.getAddress())
                .privacyPolicy(settings.getPrivacyPolicy())
                .termsAndConditions(settings.getTermsAndConditions())
                .build();
    }

    public SystemSettings toEntity(SystemSettingsDto settingsDto) {
        if (settingsDto == null) {
            return null;
        }

        return SystemSettings.builder()
                .id(1L) // Usually only one system settings record
                .maintenanceMode(settingsDto.isMaintenanceMode())
                .registrationEnabled(settingsDto.isRegistrationEnabled())
                .siteName(settingsDto.getSiteName())
                .siteLogoUrl(settingsDto.getSiteLogoUrl())
                .siteFaviconUrl(settingsDto.getSiteFaviconUrl())
                .contactEmail(settingsDto.getContactEmail())
                .contactPhone(settingsDto.getContactPhone())
                .address(settingsDto.getAddress())
                .privacyPolicy(settingsDto.getPrivacyPolicy())
                .termsAndConditions(settingsDto.getTermsAndConditions())
                .build();
    }
}
