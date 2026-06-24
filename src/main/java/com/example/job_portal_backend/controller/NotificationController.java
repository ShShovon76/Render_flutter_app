package com.example.job_portal_backend.controller;

import com.example.job_portal_backend.dtos.other.NotificationDto;
import com.example.job_portal_backend.dtos.searchAndPaginaton.PageResponse;
import com.example.job_portal_backend.security.UserPrincipal;
import com.example.job_portal_backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<PageResponse<NotificationDto>> getUserNotifications(
            @AuthenticationPrincipal UserPrincipal user,
            Pageable pageable
    ) {
        Page<NotificationDto> page = notificationService.getUserNotifications(user.getId(), pageable);

        return ResponseEntity.ok(
                PageResponse.of(
                        page.getContent(),
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements()
                )
        );
    }


    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications(@RequestParam Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    @PostMapping
    public ResponseEntity<NotificationDto> createNotification(@RequestBody NotificationDto notificationDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationService.createNotification(notificationDto));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationDto> markRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(notificationService.markAsRead(id, user.getId()));
    }


    @PutMapping("/read-all")
    public ResponseEntity<Integer> markAll(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(notificationService.markAllAsRead(user.getId()));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        notificationService.deleteNotification(id, user.getId());
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/unread-count")
    public ResponseEntity<Long> unreadCount(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationCount(user.getId()));
    }

}