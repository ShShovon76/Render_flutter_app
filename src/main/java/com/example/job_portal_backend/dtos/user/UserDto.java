package com.example.job_portal_backend.dtos.user;

import com.example.job_portal_backend.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private UserRole role;
    private String profilePictureUrl;
    private LocalDateTime createdAt;
}
