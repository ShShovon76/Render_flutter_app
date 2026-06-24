package com.example.job_portal_backend.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminAuditLogDto {
    private Long id;
    private Long adminUserId;
    private Long targetUserId;
    private String action;
    private String details;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime timestamp;
}
