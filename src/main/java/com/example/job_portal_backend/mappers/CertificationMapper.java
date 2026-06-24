package com.example.job_portal_backend.mappers;

import com.example.job_portal_backend.dtos.jobseeker.CertificationDto;
import com.example.job_portal_backend.entity.Certification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CertificationMapper {

    public CertificationDto toDto(Certification certification) {
        if (certification == null) {
            return null;
        }

        return CertificationDto.builder()
                .id(certification.getId())
                .title(certification.getTitle())
                .issuer(certification.getIssuer())
                .issueDate(certification.getIssueDate())
                .expiryDate(certification.getExpiryDate())
                .credentialUrl(certification.getCredentialUrl())
                .build();
    }

    public Certification toEntity(CertificationDto certificationDto) {
        if (certificationDto == null) {
            return null;
        }

        return Certification.builder()
                .id(certificationDto.getId())
                .title(certificationDto.getTitle())
                .issuer(certificationDto.getIssuer())
                .issueDate(certificationDto.getIssueDate())
                .expiryDate(certificationDto.getExpiryDate())
                .credentialUrl(certificationDto.getCredentialUrl())
                .build();
    }
    public List<CertificationDto> toDtoList(List<Certification> certificationList) {
        if (certificationList == null) return new ArrayList<>();
        return certificationList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
