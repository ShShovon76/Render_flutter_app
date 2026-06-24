package com.example.job_portal_backend.dtos.user;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String fullName;

    @Email(message = "Email should be valid")
    private String email;

    private String phone;
    private String profilePictureUrl;
}
