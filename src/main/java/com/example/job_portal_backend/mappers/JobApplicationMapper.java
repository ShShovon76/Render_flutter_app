package com.example.job_portal_backend.mappers;

import com.example.job_portal_backend.dtos.job.JobApplicationDto;
import com.example.job_portal_backend.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobApplicationMapper {

    public JobApplicationDto toDto(JobApplication application) {

        JobSeekerProfile js = application.getJobSeeker();
        User user = js != null ? js.getUser() : null;
        Resume resume = application.getResume();

        return JobApplicationDto.builder()
                .id(application.getId())
                .jobId(application.getJob().getId())
                .jobSeekerId(js != null ? js.getId() : null)
                .resumeId(resume != null ? resume.getId() : null)
                .resumeUrl(resume != null ? resume.getFileUrl() : null)
                .resumeTitle(resume != null ? resume.getTitle() : null)
                .coverLetter(application.getCoverLetter())
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .jobTitle(application.getJob().getTitle())
                .companyName(application.getJob().getCompany().getName())
                .jobSeekerName(user != null ? user.getFullName() : null)
                .jobSeekerEmail(user != null ? user.getEmail() : null)
                .jobSeekerProfilePicture(user != null ? user.getProfilePictureUrl() : null)
                .build();
    }

}
