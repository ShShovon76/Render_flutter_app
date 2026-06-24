package com.example.job_portal_backend.controller;

import com.example.job_portal_backend.dtos.other.ConversationDto;
import com.example.job_portal_backend.dtos.other.MessageDto;
import com.example.job_portal_backend.dtos.other.SendMessageRequest;
import com.example.job_portal_backend.service.MessagingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessagingController {
    private final MessagingService messagingService;

    @GetMapping("/conversations")
    public ResponseEntity<Page<ConversationDto>> getUserConversations(
            @RequestParam Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(messagingService.getUserConversations(userId, pageable));
    }

    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<ConversationDto> getConversation(
            @PathVariable Long conversationId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(messagingService.getConversation(conversationId, userId));
    }

    @GetMapping("/conversations/between")
    public ResponseEntity<ConversationDto> getConversationBetweenUsers(
            @RequestParam Long user1Id,
            @RequestParam Long user2Id) {
        return ResponseEntity.ok(messagingService.getConversationBetweenUsers(user1Id, user2Id));
    }

    @PostMapping("/send")
    public ResponseEntity<MessageDto> sendMessage(
            @Valid @RequestBody SendMessageRequest request,
            @RequestParam Long senderId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messagingService.sendMessage(request, senderId));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<Page<MessageDto>> getConversationMessages(
            @PathVariable Long conversationId,
            @RequestParam Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(messagingService.getConversationMessages(conversationId, userId, pageable));
    }

    @PutMapping("/messages/{messageId}/read")
    public ResponseEntity<MessageDto> markMessageAsRead(
            @PathVariable Long messageId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(messagingService.markMessageAsRead(messageId, userId));
    }

    @PutMapping("/conversations/{conversationId}/read-all")
    public ResponseEntity<Integer> markAllMessagesAsRead(
            @PathVariable Long conversationId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(messagingService.markAllMessagesAsRead(conversationId, userId));
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long messageId,
            @RequestParam Long userId) {
        messagingService.deleteMessage(messageId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadMessageCount(@RequestParam Long userId) {
        return ResponseEntity.ok(messagingService.getUnreadMessageCount(userId));
    }
}
