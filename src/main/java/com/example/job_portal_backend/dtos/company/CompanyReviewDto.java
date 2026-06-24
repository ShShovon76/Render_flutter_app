package com.example.job_portal_backend.dtos.company;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyReviewDto {
    private Long id;

    @NotNull(message = "Company ID is required")
    private Long companyId;

    @NotNull(message = "Reviewer ID is required")
    private Long reviewerId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Comment is required")
    private String comment;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    // For response only
    private String reviewerName;
    private String reviewerProfilePicture;
}
