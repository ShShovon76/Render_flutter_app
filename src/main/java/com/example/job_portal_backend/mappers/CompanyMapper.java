package com.example.job_portal_backend.mappers;

import com.example.job_portal_backend.dtos.company.CompanyDto;
import com.example.job_portal_backend.dtos.company.OwnerDto;
import com.example.job_portal_backend.dtos.company.SocialLinkDto;
import com.example.job_portal_backend.entity.Company;
import com.example.job_portal_backend.entity.SocialLink;
import com.example.job_portal_backend.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CompanyMapper {

    public CompanyDto toDto(Company company) {
        if (company == null) return null;

        List<SocialLinkDto> socialLinks = company.getSocialLinks() == null
                ? List.of()
                : company.getSocialLinks().stream()
                .map(this::toSocialLinkDto)
                .toList();

        // Create OwnerDto from company owner
        OwnerDto ownerDto = null;
        if (company.getOwner() != null) {
            ownerDto = OwnerDto.builder()
                    .id(company.getOwner().getId())
                    .fullName(company.getOwner().getFullName())
                    .build();
        }

        return CompanyDto.builder()
                .id(company.getId())
                .owner(ownerDto)
                .name(company.getName())
                .industry(company.getIndustry())
                .companySize(company.getCompanySize())
                .logoUrl(company.getLogoUrl())
                .coverImageUrl(company.getCoverImageUrl())
                .about(company.getAbout())
                .website(company.getWebsite())
                .email(company.getEmail())
                .phone(company.getPhone())
                .address(company.getAddress())
                .foundedYear(company.getFoundedYear())
                .socialLinks(socialLinks)
                .rating(company.getRating())
                .verified(company.isVerified())
                .reviewCount(company.getReviewCount())
                .build();
    }

    // ✅ CREATE - Need to accept User owner parameter
    public Company toEntityForCreate(CompanyDto dto, User owner) {
        if (dto == null) return null;

        return Company.builder()
                .name(dto.getName())
                .industry(dto.getIndustry())
                .companySize(dto.getCompanySize())
                .logoUrl(dto.getLogoUrl())
                .coverImageUrl(dto.getCoverImageUrl())
                .about(dto.getAbout())
                .website(dto.getWebsite())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .foundedYear(dto.getFoundedYear())
                .owner(owner)  // ✅ Set the owner relationship
                .build();
    }

    // ✅ UPDATE - Add owner update if needed
    public void updateEntity(CompanyDto dto, Company entity) {
        if (dto == null || entity == null) return;

        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getIndustry() != null) entity.setIndustry(dto.getIndustry());
        if (dto.getCompanySize() != null) entity.setCompanySize(dto.getCompanySize());
        if (dto.getLogoUrl() != null) entity.setLogoUrl(dto.getLogoUrl());
        if (dto.getCoverImageUrl() != null) entity.setCoverImageUrl(dto.getCoverImageUrl());
        if (dto.getAbout() != null) entity.setAbout(dto.getAbout());
        if (dto.getWebsite() != null) entity.setWebsite(dto.getWebsite());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getPhone() != null) entity.setPhone(dto.getPhone());
        if (dto.getAddress() != null) entity.setAddress(dto.getAddress());
        if (dto.getFoundedYear() != null) entity.setFoundedYear(dto.getFoundedYear());

        // Typically don't update owner in update operations
        // Owner changes should be separate endpoint
    }

    private SocialLinkDto toSocialLinkDto(SocialLink socialLink) {
        return SocialLinkDto.builder()
                .id(socialLink.getId())
                .type(socialLink.getType())
                .url(socialLink.getUrl())
                .build();
    }
}

