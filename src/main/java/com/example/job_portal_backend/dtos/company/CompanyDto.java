package com.example.job_portal_backend.dtos.company;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    private Long id;
    private OwnerDto owner;

    @NotBlank(message = "Company name is required")
    private String name;

    @NotBlank(message = "Industry is required")
    private String industry;

    private String companySize;
    private String logoUrl;
    private String coverImageUrl;
    private String about;
    private String website;
    private String email;
    private String phone;
    private String address;
    private Integer foundedYear;
    private List<SocialLinkDto> socialLinks;
    private BigDecimal rating;
    private boolean verified;
    private Integer reviewCount;
}
