package com.example.job_portal_backend.mappers;

import com.example.job_portal_backend.dtos.other.MessageDto;
import com.example.job_portal_backend.entity.Conversation;
import com.example.job_portal_backend.entity.Message;
import com.example.job_portal_backend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageDto toDto(Message message) {
        if (message == null) {
            return null;
        }

        return MessageDto.builder()
                .id(message.getId())
                .senderId(message.getSender() != null ? message.getSender().getId() : null)
                .receiverId(message.getReceiver() != null ? message.getReceiver().getId() : null)
                .senderName(message.getSender() != null ? message.getSender().getFullName() : null)
                .senderAvatar(message.getSender() != null ?
                        message.getSender().getProfilePictureUrl() : null)
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .isRead(message.isRead())
                .build();
    }

    public Message toEntity(MessageDto messageDto) {
        if (messageDto == null) {
            return null;
        }

        Message message = Message.builder()
                .id(messageDto.getId())
                .content(messageDto.getContent())
                .createdAt(messageDto.getCreatedAt())
                .isRead(messageDto.isRead())
                .build();

        if (messageDto.getSenderId() != null) {
            User sender = new User();
            sender.setId(messageDto.getSenderId());
            message.setSender(sender);
        }

        if (messageDto.getReceiverId() != null) {
            User receiver = new User();
            receiver.setId(messageDto.getReceiverId());
            message.setReceiver(receiver);
        }

        if (messageDto.getId() != null) {
            Conversation conversation = new Conversation();
            // Conversation will be set in service layer
            message.setConversation(conversation);
        }

        return message;
    }
}