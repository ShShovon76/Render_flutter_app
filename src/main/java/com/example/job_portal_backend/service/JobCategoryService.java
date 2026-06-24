package com.example.job_portal_backend.service;

import com.example.job_portal_backend.dtos.job.JobCategoryDto;
import com.example.job_portal_backend.entity.JobCategory;
import com.example.job_portal_backend.exceptions.ResourceNotFoundException;
import com.example.job_portal_backend.mappers.JobCategoryMapper;
import com.example.job_portal_backend.repository.JobCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobCategoryService {

    private final JobCategoryRepository categoryRepository;
    private final JobCategoryMapper categoryMapper;

    public List<JobCategoryDto> getAllCategories() {
        // Switch from findAll() to your optimized query
        return categoryRepository.findAllWithCounts()
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }
    public Page<JobCategoryDto> getTopCategories(Pageable pageable) {
        return categoryRepository.findTopCategoriesByJobCount(pageable)
                .map(categoryMapper::toDto);
    }

    public Page<JobCategoryDto> getCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toDto);
    }

    public JobCategoryDto getCategoryById(Long id) {
        JobCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return categoryMapper.toDto(category);
    }

    public JobCategory getCategoryEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Transactional
    public JobCategoryDto createCategory(JobCategoryDto categoryDto) {
        // Check if category name already exists
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new IllegalArgumentException("Category name already exists: " + categoryDto.getName());
        }

        JobCategory category = categoryMapper.toEntity(categoryDto);
        JobCategory savedCategory = categoryRepository.save(category);

        log.info("Category created: {}", savedCategory.getName());
        return categoryMapper.toDto(savedCategory);
    }


    @Transactional
    public JobCategoryDto updateCategory(Long id, JobCategoryDto categoryDto) {
        JobCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Check name uniqueness if changing name
        if (categoryDto.getName() != null && !categoryDto.getName().equals(category.getName())) {
            if (categoryRepository.existsByName(categoryDto.getName())) {
                throw new IllegalArgumentException("Category name already exists: " + categoryDto.getName());
            }
            category.setName(categoryDto.getName());
        }

        if (categoryDto.getDescription() != null) {
            category.setDescription(categoryDto.getDescription());
        }

        JobCategory updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        JobCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Check if category has jobs
        if (!category.getJobs().isEmpty()) {
            throw new IllegalStateException("Cannot delete category with existing jobs");
        }

        categoryRepository.delete(category);
        log.info("Category deleted: {}", id);
    }

    public JobCategoryDto getCategoryByName(String name) {
        JobCategory category = categoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with name: " + name));
        return categoryMapper.toDto(category);
    }
}