package com.example.job_portal_backend.mappers;

import com.example.job_portal_backend.dtos.user.UserDto;
import com.example.job_portal_backend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .profilePictureUrl(user.getProfilePictureUrl())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        return User.builder()
                .id(userDto.getId())
                .fullName(userDto.getFullName())
                .email(userDto.getEmail())
                .phone(userDto.getPhone())
                .role(userDto.getRole())
                .profilePictureUrl(userDto.getProfilePictureUrl())
                .build();
    }
}