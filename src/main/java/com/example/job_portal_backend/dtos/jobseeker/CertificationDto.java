package com.example.job_portal_backend.dtos.jobseeker;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificationDto {
    private Long id;
    private String title;
    private String issuer;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate issueDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    private String credentialUrl;
}
