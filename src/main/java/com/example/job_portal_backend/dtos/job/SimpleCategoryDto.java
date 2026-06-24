package com.example.job_portal_backend.dtos.job;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleCategoryDto {
    private Long id;
    private String name;
}