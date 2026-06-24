package com.example.job_portal_backend.controller;

import com.example.job_portal_backend.dtos.job.JobResponseDto;
import com.example.job_portal_backend.dtos.searchAndPaginaton.JobSearchFilter;
import com.example.job_portal_backend.entity.JobCategory;
import com.example.job_portal_backend.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/jobs")
    public ResponseEntity<Page<JobResponseDto>> searchJobs(
            @RequestBody JobSearchFilter filter
    ) {
        return ResponseEntity.ok(searchService.searchJobs(filter));
    }

    @GetMapping("/autocomplete/job-titles")
    public ResponseEntity<List<String>> autocompleteJobTitles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(searchService.autocompleteJobTitles(keyword, limit));
    }

    @GetMapping("/autocomplete/companies")
    public ResponseEntity<List<String>> autocompleteCompanies(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(searchService.autocompleteCompanies(keyword, limit));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<JobCategory>> getAllCategories() {
        return ResponseEntity.ok(searchService.getAllCategories());
    }

    @GetMapping("/popular-skills")
    public ResponseEntity<List<String>> getPopularSkills(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(searchService.getPopularSkills(limit));
    }
}
