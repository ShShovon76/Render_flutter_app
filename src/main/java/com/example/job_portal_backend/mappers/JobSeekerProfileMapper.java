package com.example.job_portal_backend.mappers;

import com.example.job_portal_backend.dtos.job.ResumeDto;
import com.example.job_portal_backend.dtos.jobseeker.CertificationDto;
import com.example.job_portal_backend.dtos.jobseeker.EducationDto;
import com.example.job_portal_backend.dtos.jobseeker.ExperienceDto;
import com.example.job_portal_backend.dtos.jobseeker.JobSeekerProfileDto;
import com.example.job_portal_backend.entity.JobSeekerProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JobSeekerProfileMapper {

    private final EducationMapper educationMapper;
    private final ExperienceMapper experienceMapper;
    private final CertificationMapper certificationMapper;
    private final ResumeMapper resumeMapper;

    public JobSeekerProfileDto toDto(JobSeekerProfile profile) {
        if (profile == null) return null;

        return JobSeekerProfileDto.builder()
                .id(profile.getId())
                .userId(profile.getUser() != null ? profile.getUser().getId() : null)
                .headline(profile.getHeadline())
                .summary(profile.getSummary())
                .skills(profile.getSkills())
                .portfolioLinks(profile.getPortfolioLinks())
                .preferredJobTypes(profile.getPreferredJobTypes())
                .preferredLocations(profile.getPreferredLocations())

                .education(profile.getEducation() == null ? List.of()
                        : profile.getEducation().stream().map(educationMapper::toDto).toList())

                .experience(profile.getExperience() == null ? List.of()
                        : profile.getExperience().stream().map(experienceMapper::toDto).toList())

                .certifications(profile.getCertifications() == null ? List.of()
                        : profile.getCertifications().stream().map(certificationMapper::toDto).toList())

                .resumes(profile.getResumes() == null ? List.of()
                        : profile.getResumes().stream().map(resumeMapper::toDto).toList())

                .build();
    }

    public JobSeekerProfile toEntity(JobSeekerProfileDto dto) {
        if (dto == null) return null;

        return JobSeekerProfile.builder()
                .id(dto.getId())
                .headline(dto.getHeadline())
                .summary(dto.getSummary())
                .skills(dto.getSkills())
                .portfolioLinks(dto.getPortfolioLinks())
                .preferredJobTypes(dto.getPreferredJobTypes())
                .preferredLocations(dto.getPreferredLocations())
                .build();
    }
}
