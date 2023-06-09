package com.example.chatservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessage {
    private String username;
    private String sessionId;
    private String channel;
    private Long timestamp;
    private String payload;
}
