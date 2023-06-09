package com.example.chatservice.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChannelUser {
    private String channel;
    private String username;
    private Boolean isActive;
}
