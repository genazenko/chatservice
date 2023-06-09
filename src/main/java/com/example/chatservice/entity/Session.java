package com.example.chatservice.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Session {
    private String sessionId;
    private String username;
    private Boolean isAuthorized;
    private Boolean isActive;
}
