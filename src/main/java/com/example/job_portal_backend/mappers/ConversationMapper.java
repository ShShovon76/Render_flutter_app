package com.example.job_portal_backend.mappers;

import com.example.job_portal_backend.dtos.other.ConversationDto;
import com.example.job_portal_backend.entity.Conversation;
import com.example.job_portal_backend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ConversationMapper {

    public ConversationDto toDto(Conversation conversation) {
        if (conversation == null) {
            return null;
        }

        return ConversationDto.builder()
                .id(conversation.getId())
                .user1Id(conversation.getUser1() != null ? conversation.getUser1().getId() : null)
                .user2Id(conversation.getUser2() != null ? conversation.getUser2().getId() : null)
                .user1Name(conversation.getUser1() != null ? conversation.getUser1().getFullName() : null)
                .user2Name(conversation.getUser2() != null ? conversation.getUser2().getFullName() : null)
                .user1Avatar(conversation.getUser1() != null ?
                        conversation.getUser1().getProfilePictureUrl() : null)
                .user2Avatar(conversation.getUser2() != null ?
                        conversation.getUser2().getProfilePictureUrl() : null)
                .lastMessage(conversation.getLastMessage())
                .lastUpdated(conversation.getLastUpdated())
                .build();
    }

    public Conversation toEntity(ConversationDto conversationDto) {
        if (conversationDto == null) {
            return null;
        }

        Conversation conversation = Conversation.builder()
                .id(conversationDto.getId())
                .lastMessage(conversationDto.getLastMessage())
                .lastUpdated(conversationDto.getLastUpdated())
                .build();

        if (conversationDto.getUser1Id() != null) {
            User user1 = new User();
            user1.setId(conversationDto.getUser1Id());
            conversation.setUser1(user1);
        }

        if (conversationDto.getUser2Id() != null) {
            User user2 = new User();
            user2.setId(conversationDto.getUser2Id());
            conversation.setUser2(user2);
        }

        return conversation;
    }
}