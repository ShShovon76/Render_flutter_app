package com.example.job_portal_backend.controller;

import com.example.job_portal_backend.dtos.job.JobCategoryDto;
import com.example.job_portal_backend.service.JobCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class JobCategoryController {

    private final JobCategoryService categoryService;

    @GetMapping("/all")
    public ResponseEntity<List<JobCategoryDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping
    public ResponseEntity<Page<JobCategoryDto>> getCategories(Pageable pageable) {
        return ResponseEntity.ok(categoryService.getCategories(pageable));
    }

    @GetMapping("/top")
    public ResponseEntity<Page<JobCategoryDto>> getTopCategories(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(categoryService.getTopCategories(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobCategoryDto> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<JobCategoryDto> getCategoryByName(@PathVariable String name) {
        return ResponseEntity.ok(categoryService.getCategoryByName(name));
    }

    @PostMapping
    public ResponseEntity<JobCategoryDto> createCategory(@Valid @RequestBody JobCategoryDto categoryDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(categoryDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobCategoryDto> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody JobCategoryDto categoryDto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
