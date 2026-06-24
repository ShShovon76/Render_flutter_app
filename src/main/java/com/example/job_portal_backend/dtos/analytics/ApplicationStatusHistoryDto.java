package com.example.job_portal_backend.dtos.analytics;

import com.example.job_portal_backend.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationStatusHistoryDto {
    private Long id;
    private Long applicationId;
    private ApplicationStatus fromStatus;
    private ApplicationStatus toStatus;
    private String note;
    private LocalDateTime changedAt;
    private Long changedByUserId;
}
