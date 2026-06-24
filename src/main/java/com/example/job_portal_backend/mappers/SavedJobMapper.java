package com.example.job_portal_backend.mappers;

import com.example.job_portal_backend.dtos.other.SavedJobDto;
import com.example.job_portal_backend.entity.Job;
import com.example.job_portal_backend.entity.JobSeekerProfile;
import com.example.job_portal_backend.entity.SavedJob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SavedJobMapper {

    private final JobMapper jobMapper;

    public SavedJobDto toDto(SavedJob savedJob) {
        if (savedJob == null) return null;

        return SavedJobDto.builder()
                .id(savedJob.getId())
                .jobId(savedJob.getJob() != null ? savedJob.getJob().getId() : null)
                // Fix: Ensure you are accessing the correct ID field for the JobSeeker
                .jobSeekerId(savedJob.getJobSeeker() != null ? savedJob.getJobSeeker().getId() : null)
                .savedAt(savedJob.getSavedAt())
                // Nesting the full job details using the JobMapper
                .job(jobMapper.toResponseDto(savedJob.getJob()))
                .build();
    }

    public SavedJob toEntity(SavedJobDto savedJobDto) {
        if (savedJobDto == null) return null;

        return SavedJob.builder()
                .id(savedJobDto.getId())
                .savedAt(savedJobDto.getSavedAt())
                // Do not instantiate Job/JobSeeker here if you plan to fetch them from DB
                .build();
    }
}