package com.example.job_portal_backend.mappers;

import com.example.job_portal_backend.dtos.jobseeker.EducationDto;
import com.example.job_portal_backend.entity.Education;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EducationMapper {

    public EducationDto toDto(Education education) {
        if (education == null) {
            return null;
        }

        return EducationDto.builder()
                .id(education.getId())
                .degree(education.getDegree())
                .institution(education.getInstitution())
                .startDate(education.getStartDate())
                .endDate(education.getEndDate())
                .grade(education.getGrade())
                .build();
    }

    public Education toEntity(EducationDto educationDto) {
        if (educationDto == null) {
            return null;
        }

        Education.EducationBuilder builder = Education.builder()
                .degree(educationDto.getDegree())
                .institution(educationDto.getInstitution())
                .startDate(educationDto.getStartDate())
                .endDate(educationDto.getEndDate())
                .grade(educationDto.getGrade());

        // Only set ID if it exists (not null and greater than 0)
        if (educationDto.getId() != null && educationDto.getId() > 0) {
            builder.id(educationDto.getId());
        }

        return builder.build();
    }
    public List<EducationDto> toDtoList(List<Education> educationList) {
        if (educationList == null) return new ArrayList<>();
        return educationList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}