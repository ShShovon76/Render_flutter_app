package com.example.job_portal_backend.service;

import com.example.job_portal_backend.dtos.company.SavedCompanyDto;
import com.example.job_portal_backend.entity.Company;
import com.example.job_portal_backend.entity.JobSeekerProfile;
import com.example.job_portal_backend.entity.SavedCompany;
import com.example.job_portal_backend.exceptions.ResourceNotFoundException;
import com.example.job_portal_backend.mappers.SavedCompanyMapper;
import com.example.job_portal_backend.repository.SavedCompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SavedCompanyService {

    private final SavedCompanyRepository savedCompanyRepository;
    private final CompanyService companyService;
    private final JobSeekerProfileService profileService;
    private final SavedCompanyMapper savedCompanyMapper;

    public Page<SavedCompanyDto> getSavedCompanies(Long userId, Pageable pageable) {
        JobSeekerProfile profile = profileService.getOrCreateProfileEntity(userId);
        return savedCompanyRepository.findByJobSeeker(profile, pageable)
                .map(savedCompanyMapper::toDto);
    }

    public boolean isCompanySaved(Long companyId, Long userId) {
        JobSeekerProfile profile = profileService.getOrCreateProfileEntity(userId);
        Company company = companyService.getCompanyEntity(companyId);

        return savedCompanyRepository.existsByCompanyAndJobSeeker(company, profile);
    }

    @Transactional
    public SavedCompanyDto saveCompany(Long companyId, Long userId) {
        JobSeekerProfile profile = profileService.getOrCreateProfileEntity(userId);
        Company company = companyService.getCompanyEntity(companyId);

        // Check if already saved
        if (savedCompanyRepository.existsByCompanyAndJobSeeker(company, profile)) {
            throw new IllegalArgumentException("Company already saved");
        }

        SavedCompany savedCompany = SavedCompany.builder()
                .company(company)
                .jobSeeker(profile)
                .build();

        SavedCompany saved = savedCompanyRepository.save(savedCompany);
        log.info("Company saved: {} by user: {}", companyId, userId);

        return savedCompanyMapper.toDto(saved);
    }

    @Transactional
    public void unsaveCompany(Long companyId, Long userId) {
        JobSeekerProfile profile = profileService.getOrCreateProfileEntity(userId);
        Company company = companyService.getCompanyEntity(companyId);

        savedCompanyRepository.deleteByCompanyAndJobSeeker(company, profile);
        log.info("Company unsaved: {} by user: {}", companyId, userId);
    }

    @Transactional
    public void unsaveCompanyById(Long savedCompanyId, Long userId) {
        SavedCompany savedCompany = savedCompanyRepository.findById(savedCompanyId)
                .orElseThrow(() -> new ResourceNotFoundException("Saved company not found with id: " + savedCompanyId));

        // Verify ownership
        if (!savedCompany.getJobSeeker().getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Saved company not found for user");
        }

        savedCompanyRepository.delete(savedCompany);
        log.info("Saved company removed: {} by user: {}", savedCompanyId, userId);
    }

    public long getSaveCountForCompany(Long companyId) {
        Company company = companyService.getCompanyEntity(companyId);
        return savedCompanyRepository.countByCompany(company);
    }
}