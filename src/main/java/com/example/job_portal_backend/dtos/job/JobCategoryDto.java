package com.example.job_portal_backend.dtos.job;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobCategoryDto {
    private Long id;

    @NotBlank(message = "Category name is required")
    private String name;
    private String description;
    private Long jobCount;
}
