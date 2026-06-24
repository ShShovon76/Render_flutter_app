package com.example.job_portal_backend.entity;

import com.example.job_portal_backend.enums.JobStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
    private String icon;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Job> jobs = new ArrayList<>();

    @Transient
    @Builder.Default
    private Long jobCount = 0L;
}
