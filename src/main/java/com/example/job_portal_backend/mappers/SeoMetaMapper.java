package com.example.job_portal_backend.mappers;

import com.example.job_portal_backend.dtos.system.SeoMetaDto;
import com.example.job_portal_backend.entity.SeoMeta;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeoMetaMapper {

    public SeoMetaDto toDto(SeoMeta seoMeta) {
        if (seoMeta == null) {
            return null;
        }

        return SeoMetaDto.builder()
                .id(seoMeta.getId())
                .page(seoMeta.getPage())
                .metaTitle(seoMeta.getMetaTitle())
                .metaDescription(seoMeta.getMetaDescription())
                .keywords(seoMeta.getKeywords())
                .canonicalUrl(seoMeta.getCanonicalUrl())
                .ogTitle(seoMeta.getOgTitle())
                .ogDescription(seoMeta.getOgDescription())
                .ogImageUrl(seoMeta.getOgImageUrl())
                .twitterCard(seoMeta.getTwitterCard())
                .build();
    }

    public SeoMeta toEntity(SeoMetaDto seoMetaDto) {
        if (seoMetaDto == null) {
            return null;
        }

        return SeoMeta.builder()
                .id(seoMetaDto.getId())
                .page(seoMetaDto.getPage())
                .metaTitle(seoMetaDto.getMetaTitle())
                .metaDescription(seoMetaDto.getMetaDescription())
                .keywords(seoMetaDto.getKeywords())
                .canonicalUrl(seoMetaDto.getCanonicalUrl())
                .ogTitle(seoMetaDto.getOgTitle())
                .ogDescription(seoMetaDto.getOgDescription())
                .ogImageUrl(seoMetaDto.getOgImageUrl())
                .twitterCard(seoMetaDto.getTwitterCard())
                .build();
    }
}
