package com.example.job_portal_backend.repository;

import com.example.job_portal_backend.entity.Notification;
import com.example.job_portal_backend.entity.User;
import com.example.job_portal_backend.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUser(User user, Pageable pageable);

    Page<Notification> findByUserAndIsRead(User user, boolean isRead, Pageable pageable);

    Page<Notification> findByUserAndType(User user, NotificationType type, Pageable pageable);

    List<Notification> findByUserAndIsReadFalse(User user);

    long countByUserAndIsReadFalse(User user);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user = :user AND n.isRead = false")
    int markAllAsRead(@Param("user") User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.createdAt < :dateBefore")
    int deleteOldNotifications(@Param("dateBefore") LocalDateTime dateBefore);

    @Query("SELECT n FROM Notification n WHERE n.user = :user ORDER BY n.createdAt DESC")
    Page<Notification> findLatestNotifications(@Param("user") User user, Pageable pageable);
}
