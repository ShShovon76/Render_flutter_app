package com.example.job_portal_backend.dtos.job;

import lombok.Data;

@Data
public class JobViewRequest {
    private Long viewerId;
    private String ipAddress;
    private String userAgent;
}

