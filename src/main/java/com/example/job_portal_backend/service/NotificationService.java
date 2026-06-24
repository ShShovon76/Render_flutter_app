package com.example.job_portal_backend.service;

import com.example.job_portal_backend.dtos.other.NotificationDto;
import com.example.job_portal_backend.entity.Job;
import com.example.job_portal_backend.entity.JobApplication;
import com.example.job_portal_backend.entity.Notification;
import com.example.job_portal_backend.entity.User;
import com.example.job_portal_backend.enums.NotificationType;
import com.example.job_portal_backend.enums.UserRole;
import com.example.job_portal_backend.exceptions.ResourceNotFoundException;
import com.example.job_portal_backend.mappers.NotificationMapper;
import com.example.job_portal_backend.repository.NotificationRepository;
import com.example.job_portal_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    public Page<NotificationDto> getUserNotifications(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return notificationRepository.findByUser(user, pageable)
                .map(notificationMapper::toDto);
    }

    public List<NotificationDto> getUnreadNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return notificationRepository.findByUserAndIsReadFalse(user)
                .stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    public long getUnreadNotificationCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Transactional
    public NotificationDto createNotification(NotificationDto notificationDto) {
        User user = userRepository.findById(notificationDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + notificationDto.getUserId()));

        Notification notification = Notification.builder()
                .user(user)
                .title(notificationDto.getTitle())
                .message(notificationDto.getMessage())
                .type(notificationDto.getType())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .actionUrl(notificationDto.getActionUrl())
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        return notificationMapper.toDto(savedNotification);
    }

    @Transactional
    public void createApplicationNotification(User user, JobApplication application, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .title("New Job Application")
                .message(message)
                .type(NotificationType.APPLICATION_UPDATE)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .actionUrl("/employer/applications/" + application.getId())
                .build();

        notificationRepository.save(notification);
    }

    @Transactional
    public void createStatusUpdateNotification(User user, JobApplication application, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .title("Application Status Update")
                .message(message)
                .type(NotificationType.APPLICATION_UPDATE)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .actionUrl("/job-seeker/applications/" + application.getId())
                .build();

        notificationRepository.save(notification);
    }
    @Transactional
    public void createSystemNotification(
            User user,
            String title,
            String message,
            String actionUrl
    ) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(NotificationType.SYSTEM_ALERT)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .actionUrl(actionUrl)
                .build();

        notificationRepository.save(notification);
    }



    @Transactional
    public NotificationDto markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        // Verify user owns the notification
        if (!notification.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Notification not found for user");
        }

        notification.setRead(true);
        Notification updatedNotification = notificationRepository.save(notification);
        return notificationMapper.toDto(updatedNotification);
    }

    @Transactional
    public int markAllAsRead(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return notificationRepository.markAllAsRead(user);
    }

    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        // Verify user owns the notification
        if (!notification.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Notification not found for user");
        }

        notificationRepository.delete(notification);
    }

    @Transactional
    public void cleanupOldNotifications(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        int deletedCount = notificationRepository.deleteOldNotifications(cutoffDate);
        log.info("Cleaned up {} old notifications", deletedCount);
    }

    @Transactional
    public void notifyNewJobPosted(Job job) {

        List<User> jobSeekers = userRepository.findByRole(UserRole.JOB_SEEKER);

        for (User user : jobSeekers) {
            Notification notification = Notification.builder()
                    .user(user)
                    .title("New Job Posted")
                    .message("New job posted: " + job.getTitle())
                    .type(NotificationType.SYSTEM_ALERT)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .actionUrl("/jobs/" + job.getId())
                    .build();

            notificationRepository.save(notification);
        }
    }

}