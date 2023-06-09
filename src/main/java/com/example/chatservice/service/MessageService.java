package com.example.chatservice.service;

import com.example.chatservice.dto.ChatMessage;
import com.example.chatservice.entity.Session;
import com.example.chatservice.repository.ChatHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    private final ChatHistoryRepository chatHistoryRepository;

    public MessageService(ChatHistoryRepository chatHistoryRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
    }

    public ChatMessage sendMessage(String channel, String payload, Session session) {
        var chatMessage = ChatMessage.builder()
            .payload(payload)
            .username(session.getUsername())
            .sessionId(session.getSessionId())
            .channel(channel)
            .build();

        chatHistoryRepository.addMessage(chatMessage);
        return chatMessage;
    }

    public List<ChatMessage> getHistory(String channel) {
        return chatHistoryRepository.getHistory(channel);
    }
}
