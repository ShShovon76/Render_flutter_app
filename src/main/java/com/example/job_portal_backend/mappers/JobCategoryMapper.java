package com.example.job_portal_backend.mappers;


import com.example.job_portal_backend.dtos.job.JobCategoryDto;
import com.example.job_portal_backend.entity.JobCategory;
import org.springframework.stereotype.Component;

@Component
public class JobCategoryMapper {

    public JobCategoryDto toDto(JobCategory category) {
        if (category == null) {
            return null;
        }

        return JobCategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .jobCount(category.getJobs() != null ? (long) category.getJobs().size() : 0L)
                .build();
    }

    public JobCategory toEntity(JobCategoryDto categoryDto) {
        if (categoryDto == null) {
            return null;
        }

        return JobCategory.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .description(categoryDto.getDescription())
                .build();
    }
}