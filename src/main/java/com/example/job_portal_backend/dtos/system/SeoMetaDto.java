package com.example.job_portal_backend.dtos.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeoMetaDto {
    private Long id;
    private String page;
    private String metaTitle;
    private String metaDescription;
    private List<String> keywords;
    private String canonicalUrl;
    private String ogTitle;
    private String ogDescription;
    private String ogImageUrl;
    private String twitterCard;
}
