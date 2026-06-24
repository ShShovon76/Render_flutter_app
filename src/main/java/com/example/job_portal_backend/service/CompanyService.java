package com.example.job_portal_backend.service;

import com.example.job_portal_backend.dtos.company.CompanyDto;
import com.example.job_portal_backend.dtos.company.CompanyReviewDto;
import com.example.job_portal_backend.dtos.company.CompanyUpdateRequest;
import com.example.job_portal_backend.dtos.company.SocialLinkDto;
import com.example.job_portal_backend.entity.Company;
import com.example.job_portal_backend.entity.CompanyReview;
import com.example.job_portal_backend.entity.SocialLink;
import com.example.job_portal_backend.entity.User;
import com.example.job_portal_backend.exceptions.ResourceNotFoundException;
import com.example.job_portal_backend.exceptions.UnauthorizedException;
import com.example.job_portal_backend.mappers.CompanyMapper;
import com.example.job_portal_backend.mappers.CompanyReviewMapper;
import com.example.job_portal_backend.repository.CompanyRepository;
import com.example.job_portal_backend.repository.CompanyReviewRepository;
import com.example.job_portal_backend.repository.UserRepository;
import com.example.job_portal_backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final CompanyMapper companyMapper;
    private final CompanyReviewMapper reviewMapper;
    private final FileStorageService fileStorageService;

    public Page<CompanyDto> getCompanies(Pageable pageable) {
        return companyRepository.findAll(pageable)
                .map(companyMapper::toDto);
    }

    public Page<CompanyDto> searchCompanies(String keyword, String industry, Boolean verified, Pageable pageable) {
        return companyRepository.findCompaniesWithFilters(industry, verified, keyword, pageable)
                .map(companyMapper::toDto);
    }

    public CompanyDto getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
        return companyMapper.toDto(company);
    }

    public Company getCompanyEntity(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
    }

    public Page<CompanyDto> getCompaniesByOwner(Long ownerId, Pageable pageable) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + ownerId));

        return companyRepository.findByOwner(owner, pageable)
                .map(companyMapper::toDto);
    }

    public CompanyDto createCompany(
            CompanyDto dto,
            MultipartFile logo,
            UserPrincipal user
    ) throws IOException {

        User owner = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Company company = new Company();
        company.setOwner(owner);
        company.setName(dto.getName());
        company.setIndustry(dto.getIndustry());
        company.setCompanySize(dto.getCompanySize());
        company.setAbout(dto.getAbout());
        company.setWebsite(dto.getWebsite());
        company.setEmail(dto.getEmail());
        company.setPhone(dto.getPhone());
        company.setAddress(dto.getAddress());
        company.setFoundedYear(dto.getFoundedYear());

        if (logo != null && !logo.isEmpty()) {
            company.setLogoUrl(fileStorageService.storeCompanyLogo(logo));
        }

        company = companyRepository.save(company);

        return companyMapper.toDto(company);
    }

    @Transactional
    public void createCompanyForEmployer(User employer, String companyName) {
        // Option 1: Use mapper
        CompanyDto dto = CompanyDto.builder()
                .name(companyName)
                .industry("Not specified")
                .build();

        Company company = companyMapper.toEntityForCreate(dto, employer);
        company.setVerified(false);
        company.setRating(BigDecimal.ZERO);
        company.setReviewCount(0);

        companyRepository.save(company);
        log.info("Default company created for employer: {}", employer.getEmail());
    }

    public CompanyDto updateCompany(
            Long id,
            CompanyUpdateRequest request,
            MultipartFile logo,
            MultipartFile cover,
            UserPrincipal user
    ) throws IOException {

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        if (!company.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not the owner");
        }

        // basic fields
        company.setName(request.getName());
        company.setIndustry(request.getIndustry());
        company.setCompanySize(request.getCompanySize());
        company.setAbout(request.getAbout());
        company.setWebsite(request.getWebsite());
        company.setEmail(request.getEmail());
        company.setPhone(request.getPhone());
        company.setAddress(request.getAddress());
        company.setFoundedYear(request.getFoundedYear());

        // LOGO
        if (logo != null && !logo.isEmpty()) {
            fileStorageService.deleteFile(company.getLogoUrl());
            company.setLogoUrl(fileStorageService.storeCompanyLogo(logo));
        }

        // COVER
        if (cover != null && !cover.isEmpty()) {
            fileStorageService.deleteFile(company.getCoverImageUrl());
            company.setCoverImageUrl(fileStorageService.storeCompanyCover(cover));
        }
        if (request.getSocialLinks() != null) {
            company.getSocialLinks().clear();

            for (SocialLinkDto dto : request.getSocialLinks()) {
                SocialLink link = new SocialLink();
                link.setCompany(company);
                link.setType(dto.getType());
                link.setUrl(dto.getUrl());
                company.getSocialLinks().add(link);
            }
        }

        return companyMapper.toDto(companyRepository.save(company));
    }


    @Transactional
    public CompanyReviewDto addReview(Long companyId, CompanyReviewDto reviewDto, Long reviewerId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + reviewerId));

        // Check if user already reviewed this company
        boolean alreadyReviewed = reviewRepository.findByCompanyAndReviewer(company, reviewer).isPresent();
        if (alreadyReviewed) {
            throw new IllegalArgumentException("You have already reviewed this company");
        }

        CompanyReview review = CompanyReview.builder()
                .company(company)
                .reviewer(reviewer)
                .rating(reviewDto.getRating())
                .title(reviewDto.getTitle())
                .comment(reviewDto.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        CompanyReview savedReview = reviewRepository.save(review);

        // Company rating will be automatically recalculated via @PreUpdate

        log.info("Review added for company: {} by user: {}", company.getName(), reviewer.getEmail());

        return reviewMapper.toDto(savedReview);
    }

    public Page<CompanyReviewDto> getCompanyReviews(Long companyId, Pageable pageable) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        return reviewRepository.findByCompany(company, pageable)
                .map(reviewMapper::toDto);
    }

    @Transactional
    public void verifyCompany(Long companyId, Long adminId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        company.setVerified(true);
        companyRepository.save(company);

        log.info("Company verified: {} by admin: {}", company.getName(), adminId);
    }

    @Transactional
    public void deleteCompany(Long companyId, Long ownerId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        // Verify owner
        if (!company.getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedException("Only company owner can delete company");
        }

        companyRepository.delete(company);
        log.info("Company deleted: {}", companyId);
    }

    @Transactional
    public CompanyDto uploadCompanyLogo(Long companyId, MultipartFile file, UserPrincipal user) throws IOException {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        if (!company.getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedException("Only company owner can upload logo");
        }

        if (company.getLogoUrl() != null) {
            fileStorageService.deleteFile(company.getLogoUrl());
        }

        company.setLogoUrl(fileStorageService.storeCompanyLogo(file));
        return companyMapper.toDto(companyRepository.save(company));
    }


    @Transactional
    public CompanyDto uploadCompanyCover(Long companyId, MultipartFile file, UserPrincipal user) throws IOException {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        if (!company.getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedException("Only company owner can upload cover");
        }

        if (company.getCoverImageUrl() != null) {
            fileStorageService.deleteFile(company.getCoverImageUrl());
        }

        String storedPath = fileStorageService.storeCompanyCover(file);

        company.setCoverImageUrl(storedPath);

        return companyMapper.toDto(companyRepository.save(company));
    }

    public void deleteLogo(Long companyId, UserPrincipal user) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        if (!company.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Not allowed");
        }

        if (company.getLogoUrl() != null) {
            fileStorageService.deleteFile(company.getLogoUrl());
            company.setLogoUrl(null);
            companyRepository.save(company);
        }
    }

    public void deleteCover(Long companyId, UserPrincipal user) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        if (!company.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Not allowed");
        }

        if (company.getCoverImageUrl() != null) {
            fileStorageService.deleteFile(company.getCoverImageUrl());
            company.setCoverImageUrl(null);
            companyRepository.save(company);
        }
    }

    private Company getOwnedCompany(Long id, UserPrincipal user) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        if (!company.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Forbidden");
        }

        return company;
    }


}
