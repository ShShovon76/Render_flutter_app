package com.example.job_portal_backend.mappers;

import com.example.job_portal_backend.dtos.other.NotificationDto;
import com.example.job_portal_backend.entity.Notification;
import com.example.job_portal_backend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationDto toDto(Notification notification) {
        if (notification == null) {
            return null;
        }

        return NotificationDto.builder()
                .id(notification.getId())
                .userId(notification.getUser() != null ? notification.getUser().getId() : null)
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .createdAt(notification.getCreatedAt())
                .isRead(notification.isRead())
                .actionUrl(notification.getActionUrl())
                .build();
    }

    public Notification toEntity(NotificationDto notificationDto) {
        if (notificationDto == null) {
            return null;
        }

        Notification notification = Notification.builder()
                .id(notificationDto.getId())
                .title(notificationDto.getTitle())
                .message(notificationDto.getMessage())
                .type(notificationDto.getType())
                .createdAt(notificationDto.getCreatedAt())
                .isRead(notificationDto.isRead())
                .actionUrl(notificationDto.getActionUrl())
                .build();

        if (notificationDto.getUserId() != null) {
            User user = new User();
            user.setId(notificationDto.getUserId());
            notification.setUser(user);
        }

        return notification;
    }
}
