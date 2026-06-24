package com.example.job_portal_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "seo_meta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeoMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String page;

    @Column(nullable = false)
    private String metaTitle;

    @Column(length = 500)
    private String metaDescription;

    @ElementCollection
    @CollectionTable(name = "seo_keywords", joinColumns = @JoinColumn(name = "seo_meta_id"))
    @Column(name = "keyword")
    private List<String> keywords = new ArrayList<>();

    private String canonicalUrl;

    private String ogTitle;

    private String ogDescription;

    private String ogImageUrl;

    private String twitterCard;
}
