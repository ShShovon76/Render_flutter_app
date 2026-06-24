package com.example.job_portal_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_views", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"job_id", "viewer_id", "view_date"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viewer_id")
    private User viewer;

    private String ipAddress;

    private String userAgent;

    @Column(nullable = false)
    private LocalDateTime viewDate;

    @PrePersist
    protected void onCreate() {
        viewDate = LocalDateTime.now();
    }
}