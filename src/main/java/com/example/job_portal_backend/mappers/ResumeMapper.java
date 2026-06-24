package com.example.job_portal_backend.mappers;

import com.example.job_portal_backend.dtos.job.ResumeDto;
import com.example.job_portal_backend.entity.Resume;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ResumeMapper {

    public ResumeDto toDto(Resume resume) {
        return ResumeDto.builder()
                .id(resume.getId())
                .title(resume.getTitle())
                .fileUrl(resume.getFileUrl())
                .originalFileName(resume.getOriginalFileName())
                .primaryResume(resume.isPrimaryResume())
                .uploadedAt(resume.getUploadedAt())
                .build();
    }

    public List<ResumeDto> toDtoList(List<Resume> resumes) {
        if (resumes == null) return Collections.emptyList();
        return resumes.stream()
                .map(this::toDto) // Calls the method above for each item
                .collect(Collectors.toList());
    }
}

