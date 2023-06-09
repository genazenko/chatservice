package com.example.chatservice.repository;

import com.example.chatservice.dto.ChatMessage;
import com.example.chatservice.repository.impl.InMemoryChatHistoryRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ChatHistoryRepositoryTest {

    private ChatHistoryRepository chatHistoryRepository;

    @Test
    void chatHistoryRepositoryTest() {
        chatHistoryRepository = new InMemoryChatHistoryRepository(10);
        var message = ChatMessage.builder()
            .channel("channel")
            .sessionId("session")
            .username("user")
            .payload("message")
            .build();
        chatHistoryRepository.addMessage(message);
        var history = chatHistoryRepository.getHistory(message.getChannel());
        assertEquals(1, history.size());
        assertNotNull(history.get(0).getTimestamp());
        assertEquals(message.getChannel(), history.get(0).getChannel());
        assertEquals(message.getUsername(), history.get(0).getUsername());
        assertEquals(message.getPayload(), history.get(0).getPayload());
    }
}
