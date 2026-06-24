package com.example.job_portal_backend.mappers;

import com.example.job_portal_backend.dtos.jobseeker.ExperienceDto;
import com.example.job_portal_backend.entity.Experience;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExperienceMapper {

    public ExperienceDto toDto(Experience experience) {
        if (experience == null) {
            return null;
        }

        return ExperienceDto.builder()
                .id(experience.getId())
                .companyName(experience.getCompanyName())
                .jobTitle(experience.getJobTitle())
                .startDate(experience.getStartDate())
                .endDate(experience.getEndDate())
                .responsibilities(experience.getResponsibilities())
                .build();
    }

    public Experience toEntity(ExperienceDto experienceDto) {
        if (experienceDto == null) {
            return null;
        }

         Experience.ExperienceBuilder builder =Experience.builder()
                .companyName(experienceDto.getCompanyName())
                .jobTitle(experienceDto.getJobTitle())
                .startDate(experienceDto.getStartDate())
                .endDate(experienceDto.getEndDate())
                .responsibilities(experienceDto.getResponsibilities());
        if (experienceDto.getId() != null && experienceDto.getId() > 0) {
            builder.id(experienceDto.getId());
        }

        return builder.build();
    }

    public List<ExperienceDto> toDtoList(List<Experience> experienceList) {
        if (experienceList == null) return new ArrayList<>();
        return experienceList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
