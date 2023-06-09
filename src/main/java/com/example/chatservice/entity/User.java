package com.example.chatservice.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    /**
     * Should be unique
     */
    private String username;
    /**
     * Salted encrypted string
     */
    private byte[] hashedPassword;
}
