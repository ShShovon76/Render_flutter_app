package com.example.job_portal_backend.dtos.company;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompanyUpdateRequest {

    private String name;
    private String industry;
    private String companySize;
    private String about;
    private String website;
    private String email;
    private String phone;
    private String address;
    private Integer foundedYear;
    private List<SocialLinkDto> socialLinks;

}

