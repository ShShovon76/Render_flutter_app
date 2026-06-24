package com.example.job_portal_backend.repository;

import com.example.job_portal_backend.entity.Conversation;
import com.example.job_portal_backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByUser1AndUser2(User user1, User user2);

    @Query("SELECT c FROM Conversation c WHERE c.user1 = :user OR c.user2 = :user")
    Page<Conversation> findByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT c FROM Conversation c WHERE " +
            "(c.user1 = :user1 AND c.user2 = :user2) OR " +
            "(c.user1 = :user2 AND c.user2 = :user1)")
    Optional<Conversation> findConversationBetweenUsers(
            @Param("user1") User user1,
            @Param("user2") User user2
    );

    @Query("SELECT c FROM Conversation c WHERE " +
            "(c.user1 = :user OR c.user2 = :user) AND " +
            "c.lastUpdated = (SELECT MAX(c2.lastUpdated) FROM Conversation c2 WHERE c2.id = c.id)")
    Page<Conversation> findRecentConversations(@Param("user") User user, Pageable pageable);
}