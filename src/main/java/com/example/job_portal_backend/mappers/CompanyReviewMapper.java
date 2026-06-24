package com.example.job_portal_backend.mappers;

import com.example.job_portal_backend.dtos.company.CompanyReviewDto;
import com.example.job_portal_backend.entity.Company;
import com.example.job_portal_backend.entity.CompanyReview;
import com.example.job_portal_backend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CompanyReviewMapper {

    public CompanyReviewDto toDto(CompanyReview review) {
        if (review == null) {
            return null;
        }

        return CompanyReviewDto.builder()
                .id(review.getId())
                .companyId(review.getCompany() != null ? review.getCompany().getId() : null)
                .reviewerId(review.getReviewer() != null ? review.getReviewer().getId() : null)
                .rating(review.getRating())
                .title(review.getTitle())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .reviewerName(review.getReviewer() != null ? review.getReviewer().getFullName() : null)
                .reviewerProfilePicture(review.getReviewer() != null ?
                        review.getReviewer().getProfilePictureUrl() : null)
                .build();
    }

    public CompanyReview toEntity(CompanyReviewDto reviewDto) {
        if (reviewDto == null) {
            return null;
        }

        CompanyReview review = CompanyReview.builder()
                .id(reviewDto.getId())
                .rating(reviewDto.getRating())
                .title(reviewDto.getTitle())
                .comment(reviewDto.getComment())
                .createdAt(reviewDto.getCreatedAt())
                .build();

        if (reviewDto.getCompanyId() != null) {
            Company company = new Company();
            company.setId(reviewDto.getCompanyId());
            review.setCompany(company);
        }

        if (reviewDto.getReviewerId() != null) {
            User reviewer = new User();
            reviewer.setId(reviewDto.getReviewerId());
            review.setReviewer(reviewer);
        }

        return review;
    }
}