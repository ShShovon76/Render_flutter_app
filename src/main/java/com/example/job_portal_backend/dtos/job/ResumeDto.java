package com.example.job_portal_backend.dtos.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeDto {
    private Long id;
    private String title;
    private String fileUrl;
    private String originalFileName;
    private boolean primaryResume;
    private LocalDateTime uploadedAt;
}
