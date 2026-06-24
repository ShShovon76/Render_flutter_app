package com.example.job_portal_backend.controller;

import com.example.job_portal_backend.dtos.company.CompanyDto;
import com.example.job_portal_backend.dtos.company.CompanyReviewDto;
import com.example.job_portal_backend.dtos.company.CompanyUpdateRequest;
import com.example.job_portal_backend.security.UserPrincipal;
import com.example.job_portal_backend.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<Page<CompanyDto>> getCompanies(Pageable pageable) {
        return ResponseEntity.ok(companyService.getCompanies(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CompanyDto>> searchCompanies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) Boolean verified,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        // Parse the sort parameter
        String sortField = "createdAt"; // default
        Sort.Direction direction = Sort.Direction.DESC; // default

        if (sort != null && !sort.isEmpty()) {
            String[] sortParts = sort.split(",");
            if (sortParts.length > 0) {
                sortField = sortParts[0];
                if (sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1])) {
                    direction = Sort.Direction.ASC;
                }
            }
        }

        // Map frontend sort values to database column names
        if ("newest".equals(sortField)) {
            sortField = "createdAt";
            direction = Sort.Direction.DESC;
        } else if ("name".equals(sortField)) {
            sortField = "name";
            direction = Sort.Direction.ASC;
        } else if ("rating".equals(sortField)) {
            sortField = "rating";
            direction = Sort.Direction.DESC;
        }

        // Create Pageable with sorting
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        return ResponseEntity.ok(companyService.searchCompanies(keyword, industry, verified, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDto> getCompanyById(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getCompanyById(id));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<Page<CompanyDto>> getCompaniesByOwner(@PathVariable Long ownerId, Pageable pageable) {
        return ResponseEntity.ok(companyService.getCompaniesByOwner(ownerId, pageable));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CompanyDto> createCompany(
            @RequestPart("data") CompanyDto dto,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            @AuthenticationPrincipal UserPrincipal user
    ) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(companyService.createCompany(dto, logo, user));
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CompanyDto> updateCompany(
            @PathVariable Long id,
            @RequestPart("data") CompanyUpdateRequest request,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            @RequestPart(value = "cover", required = false) MultipartFile cover,
            @AuthenticationPrincipal UserPrincipal user
    ) throws IOException {
        return ResponseEntity.ok(
                companyService.updateCompany(id, request, logo, cover, user)
        );
    }


    @PostMapping("/{companyId}/reviews")
    public ResponseEntity<CompanyReviewDto> addReview(
            @PathVariable Long companyId,
            @Valid @RequestBody CompanyReviewDto reviewDto,
            @RequestParam Long reviewerId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(companyService.addReview(companyId, reviewDto, reviewerId));
    }

    @GetMapping("/{companyId}/reviews")
    public ResponseEntity<Page<CompanyReviewDto>> getCompanyReviews(
            @PathVariable Long companyId,
            Pageable pageable) {
        return ResponseEntity.ok(companyService.getCompanyReviews(companyId, pageable));
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<Void> verifyCompany(
            @PathVariable Long id,
            @RequestParam Long adminId) {
        companyService.verifyCompany(id, adminId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(
            @PathVariable Long id,
            @RequestParam Long ownerId) {
        companyService.deleteCompany(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/logo")
    public ResponseEntity<CompanyDto> uploadCompanyLogo(
            @PathVariable Long id,
            @RequestParam MultipartFile file,
            @AuthenticationPrincipal UserPrincipal user
    ) throws IOException {
        return ResponseEntity.ok(
                companyService.uploadCompanyLogo(id, file, user)
        );
    }


    @PostMapping("/{id}/cover")
    public ResponseEntity<CompanyDto> uploadCompanyCover(
            @PathVariable Long id,
            @RequestParam MultipartFile file,
            @AuthenticationPrincipal UserPrincipal user
    ) throws Exception {
        return ResponseEntity.ok(
                companyService.uploadCompanyCover(id, file, user)
        );
    }
    @DeleteMapping("/{id}/logo")
    public ResponseEntity<Void> deleteLogo(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        companyService.deleteLogo(id, user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/cover")
    public ResponseEntity<Void> deleteCover(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        companyService.deleteCover(id, user);
        return ResponseEntity.noContent().build();
    }
}
