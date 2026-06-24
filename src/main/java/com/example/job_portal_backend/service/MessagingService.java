package com.example.job_portal_backend.service;

import com.example.job_portal_backend.dtos.other.ConversationDto;
import com.example.job_portal_backend.dtos.other.MessageDto;
import com.example.job_portal_backend.dtos.other.SendMessageRequest;
import com.example.job_portal_backend.entity.Conversation;
import com.example.job_portal_backend.entity.Message;
import com.example.job_portal_backend.entity.User;
import com.example.job_portal_backend.exceptions.ResourceNotFoundException;
import com.example.job_portal_backend.exceptions.UnauthorizedException;
import com.example.job_portal_backend.mappers.ConversationMapper;
import com.example.job_portal_backend.mappers.MessageMapper;
import com.example.job_portal_backend.repository.ConversationRepository;
import com.example.job_portal_backend.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagingService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;

    public Page<ConversationDto> getUserConversations(Long userId, Pageable pageable) {
        User user = userService.getUserEntity(userId);
        return conversationRepository.findByUser(user, pageable)
                .map(conversationMapper::toDto);
    }

    public ConversationDto getConversation(Long conversationId, Long userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id: " + conversationId));

        // Verify user is part of conversation
        if (!conversation.getUser1().getId().equals(userId) &&
                !conversation.getUser2().getId().equals(userId)) {
            throw new UnauthorizedException("You are not part of this conversation");
        }

        return conversationMapper.toDto(conversation);
    }

    public ConversationDto getConversationBetweenUsers(Long user1Id, Long user2Id) {
        User user1 = userService.getUserEntity(user1Id);
        User user2 = userService.getUserEntity(user2Id);

        Optional<Conversation> conversation = conversationRepository.findConversationBetweenUsers(user1, user2);

        if (conversation.isPresent()) {
            return conversationMapper.toDto(conversation.get());
        } else {
            // Create new conversation if doesn't exist
            Conversation newConversation = Conversation.builder()
                    .user1(user1)
                    .user2(user2)
                    .lastMessage("")
                    .lastUpdated(LocalDateTime.now())
                    .build();

            Conversation saved = conversationRepository.save(newConversation);
            return conversationMapper.toDto(saved);
        }
    }

    @Transactional
    public MessageDto sendMessage(SendMessageRequest request, Long senderId) {
        User sender = userService.getUserEntity(senderId);
        User receiver = userService.getUserEntity(request.getReceiverId());

        Conversation conversation;

        if (request.getConversationId() != null) {
            // Use existing conversation
            conversation = conversationRepository.findById(request.getConversationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

            // Verify sender is part of conversation
            if (!conversation.getUser1().getId().equals(senderId) &&
                    !conversation.getUser2().getId().equals(senderId)) {
                throw new UnauthorizedException("You are not part of this conversation");
            }

            // Verify receiver is the other participant
            Long otherUserId = conversation.getUser1().getId().equals(senderId) ?
                    conversation.getUser2().getId() : conversation.getUser1().getId();

            if (!otherUserId.equals(request.getReceiverId())) {
                throw new IllegalArgumentException("Receiver does not match conversation participants");
            }
        } else {
            // Create new conversation
            conversation = conversationRepository.findConversationBetweenUsers(sender, receiver)
                    .orElseGet(() -> {
                        Conversation newConversation = Conversation.builder()
                                .user1(sender)
                                .user2(receiver)
                                .lastMessage("")
                                .lastUpdated(LocalDateTime.now())
                                .build();
                        return conversationRepository.save(newConversation);
                    });
        }

        // Create message
        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .receiver(receiver)
                .content(request.getContent())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        Message savedMessage = messageRepository.save(message);

        // Update conversation
        conversation.setLastMessage(request.getContent());
        conversation.setLastUpdated(LocalDateTime.now());
        conversationRepository.save(conversation);

        log.info("Message sent from {} to {}", senderId, request.getReceiverId());

        return messageMapper.toDto(savedMessage);
    }

    public Page<MessageDto> getConversationMessages(Long conversationId, Long userId, Pageable pageable) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        // Verify user is part of conversation
        if (!conversation.getUser1().getId().equals(userId) &&
                !conversation.getUser2().getId().equals(userId)) {
            throw new UnauthorizedException("You are not part of this conversation");
        }

        return messageRepository.findByConversationOrderByCreatedAtDesc(conversation, pageable)
                .map(messageMapper::toDto);
    }

    @Transactional
    public MessageDto markMessageAsRead(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        // Verify user is the receiver
        if (!message.getReceiver().getId().equals(userId)) {
            throw new UnauthorizedException("You are not the receiver of this message");
        }

        message.setRead(true);
        Message updatedMessage = messageRepository.save(message);

        return messageMapper.toDto(updatedMessage);
    }

    @Transactional
    public int markAllMessagesAsRead(Long conversationId, Long userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        // Verify user is part of conversation
        if (!conversation.getUser1().getId().equals(userId) &&
                !conversation.getUser2().getId().equals(userId)) {
            throw new UnauthorizedException("You are not part of this conversation");
        }

        return messageRepository.markAllMessagesAsRead(conversation.getId(), userId);
    }

    @Transactional
    public void deleteMessage(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        // Verify user is the sender
        if (!message.getSender().getId().equals(userId)) {
            throw new UnauthorizedException("Only the sender can delete this message");
        }

        messageRepository.delete(message);
        log.info("Message deleted: {} by user: {}", messageId, userId);
    }

    public long getUnreadMessageCount(Long userId) {
        return messageRepository.countUnreadMessages(userId);
    }
}
