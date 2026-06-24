package com.example.job_portal_backend.dtos.other;

import com.example.job_portal_backend.dtos.job.JobDto;
import com.example.job_portal_backend.dtos.job.JobResponseDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedJobDto {
    private Long id;
    private Long jobId;
    private Long jobSeekerId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime savedAt;

    // For response only
    private JobResponseDto job;
}
