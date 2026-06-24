package com.example.job_portal_backend.dtos.company;

import com.example.job_portal_backend.enums.SocialLinkType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialLinkDto {
    private Long id;

    @NotNull(message = "Social link type is required")
    private SocialLinkType type;

    @NotBlank(message = "URL is required")
    private String url;
}
