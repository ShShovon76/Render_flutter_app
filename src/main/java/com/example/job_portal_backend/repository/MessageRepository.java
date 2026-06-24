package com.example.job_portal_backend.repository;

import com.example.job_portal_backend.entity.Conversation;
import com.example.job_portal_backend.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByConversationOrderByCreatedAtDesc(Conversation conversation, Pageable pageable);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver.id = :userId AND m.isRead = false")
    long countUnreadMessages(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.isRead = true WHERE m.conversation.id = :conversationId AND m.receiver.id = :userId AND m.isRead = false")
    int markAllMessagesAsRead(@Param("conversationId") Long conversationId, @Param("userId") Long userId);
}
