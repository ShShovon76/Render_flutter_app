package com.example.job_portal_backend.controller;

import com.example.job_portal_backend.dtos.company.SavedCompanyDto;
import com.example.job_portal_backend.service.SavedCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/saved-companies")
@RequiredArgsConstructor
public class SavedCompanyController {

    private final SavedCompanyService savedCompanyService;

    @GetMapping
    public ResponseEntity<Page<SavedCompanyDto>> getSavedCompanies(
            @RequestParam Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(savedCompanyService.getSavedCompanies(userId, pageable));
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> isCompanySaved(
            @RequestParam Long companyId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(savedCompanyService.isCompanySaved(companyId, userId));
    }

    @PostMapping
    public ResponseEntity<SavedCompanyDto> saveCompany(
            @RequestParam Long companyId,
            @RequestParam Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedCompanyService.saveCompany(companyId, userId));
    }

    @DeleteMapping("/unsave")
    public ResponseEntity<Void> unsaveCompany(
            @RequestParam Long companyId,
            @RequestParam Long userId) {
        savedCompanyService.unsaveCompany(companyId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{savedCompanyId}")
    public ResponseEntity<Void> unsaveCompanyById(
            @PathVariable Long savedCompanyId,
            @RequestParam Long userId) {
        savedCompanyService.unsaveCompanyById(savedCompanyId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{companyId}/count")
    public ResponseEntity<Long> getSaveCountForCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(savedCompanyService.getSaveCountForCompany(companyId));
    }
}
