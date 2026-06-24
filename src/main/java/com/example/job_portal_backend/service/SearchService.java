package com.example.job_portal_backend.service;

import com.example.job_portal_backend.dtos.job.JobResponseDto;
import com.example.job_portal_backend.dtos.searchAndPaginaton.JobSearchFilter;
import com.example.job_portal_backend.dtos.searchAndPaginaton.PageResponse;
import com.example.job_portal_backend.entity.Job;
import com.example.job_portal_backend.entity.JobCategory;
import com.example.job_portal_backend.mappers.JobMapper;
import com.example.job_portal_backend.repository.JobCategoryRepository;
import com.example.job_portal_backend.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final JobRepository jobRepository;
    private final JobCategoryRepository categoryRepository;
    private final JobMapper jobMapper;

    public Page<JobResponseDto> searchJobs(JobSearchFilter filter) {

        // Extract salary safely
        Double minSalary = null;
        Double maxSalary = null;

        if (filter.getSalaryRange() != null) {
            minSalary = filter.getSalaryRange().getMin();
            maxSalary = filter.getSalaryRange().getMax();
        }

        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(Sort.Direction.DESC, "postedAt")
        );

        Page<Job> jobPage = jobRepository.searchJobs(
                filter.getKeyword(),
                filter.getLocation(),
                filter.getCategoryId(),
                filter.getJobType(),
                filter.getExperienceLevel(),
                filter.getRemote(),
                minSalary,
                maxSalary,
                pageable
        );

        return jobPage.map(jobMapper::toResponseDto);
    }

    public List<String> autocompleteJobTitles(String keyword, int limit) {
        Pageable pageable = PageRequest.of(0, limit);

        return jobRepository.searchJobs(
                        keyword, null, null, null, null, null,
                        null, null,
                        pageable
                ).getContent()
                .stream()
                .map(Job::getTitle)
                .distinct()
                .toList();
    }

    public List<String> autocompleteCompanies(String keyword, int limit) {
        Pageable pageable = PageRequest.of(0, limit);

        return jobRepository.searchJobs(
                        keyword, null, null, null, null, null,
                        null, null,
                        pageable
                ).getContent()
                .stream()
                .map(job -> job.getCompany().getName())
                .distinct()
                .toList();
    }

    public List<JobCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<String> getPopularSkills(int limit) {
        return List.of(
                "Java", "Spring Boot", "React", "Angular", "Node.js",
                "Python", "Django", "SQL", "AWS", "Docker"
        );
    }
}
