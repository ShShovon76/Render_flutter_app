package com.example.job_portal_backend.dtos.other;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDto {
    private Long id;
    private Long user1Id;
    private Long user2Id;
    private String user1Name;
    private String user2Name;
    private String user1Avatar;
    private String user2Avatar;
    private String lastMessage;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastUpdated;

    private boolean unreadMessages;
    private Long unreadCount;
}