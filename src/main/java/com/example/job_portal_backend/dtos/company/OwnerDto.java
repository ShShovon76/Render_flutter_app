package com.example.job_portal_backend.dtos.company;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OwnerDto {
    private Long id;
    private String fullName;
}