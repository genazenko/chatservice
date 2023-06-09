package com.example.chatservice.repository;

import com.example.chatservice.dto.ChatMessage;

import java.util.List;

public interface ChatHistoryRepository {
    void addMessage(ChatMessage message);

    List<ChatMessage> getHistory(String channel);
}
