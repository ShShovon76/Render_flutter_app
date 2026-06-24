package com.example.job_portal_backend.mappers;

import com.example.job_portal_backend.dtos.company.SavedCompanyDto;
import com.example.job_portal_backend.entity.Company;
import com.example.job_portal_backend.entity.JobSeekerProfile;
import com.example.job_portal_backend.entity.SavedCompany;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SavedCompanyMapper {

    private final CompanyMapper companyMapper;

    public SavedCompanyDto toDto(SavedCompany savedCompany) {
        if (savedCompany == null) {
            return null;
        }

        return SavedCompanyDto.builder()
                .id(savedCompany.getId())
                .companyId(savedCompany.getCompany() != null ? savedCompany.getCompany().getId() : null)
                .jobSeekerId(savedCompany.getJobSeeker() != null ?
                        savedCompany.getJobSeeker().getUser().getId() : null)
                .savedAt(savedCompany.getSavedAt())
                .company(savedCompany.getCompany() != null ?
                        companyMapper.toDto(savedCompany.getCompany()) : null)
                .build();
    }

    public SavedCompany toEntity(SavedCompanyDto savedCompanyDto) {
        if (savedCompanyDto == null) {
            return null;
        }

        SavedCompany savedCompany = SavedCompany.builder()
                .id(savedCompanyDto.getId())
                .savedAt(savedCompanyDto.getSavedAt())
                .build();

        if (savedCompanyDto.getCompanyId() != null) {
            Company company = new Company();
            company.setId(savedCompanyDto.getCompanyId());
            savedCompany.setCompany(company);
        }

        if (savedCompanyDto.getJobSeekerId() != null) {
            JobSeekerProfile profile = new JobSeekerProfile();
            // We'll set the actual profile in service layer
            savedCompany.setJobSeeker(profile);
        }

        return savedCompany;
    }
}