package com.example.chatservice.repository.impl;

import com.example.chatservice.dto.ChatMessage;
import com.example.chatservice.repository.ChatHistoryRepository;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class InMemoryChatHistoryRepository implements ChatHistoryRepository {
    private final Integer historyCapacity;
    private final ConcurrentMap<String, CircularFifoQueue<ChatMessage>> chatHistoryStore = new ConcurrentHashMap<>();

    public InMemoryChatHistoryRepository(@Value("${chat.history.capacity}") Integer historyCapacity) {
        this.historyCapacity = historyCapacity;
    }

    @Override
    public synchronized void addMessage(ChatMessage message) {
        message.setTimestamp(Instant.now().toEpochMilli());
        var history = chatHistoryStore.get(message.getChannel());
        if (history == null) {
            history = new CircularFifoQueue<>(historyCapacity);
        }

        history.add(message);
        chatHistoryStore.put(message.getChannel(), history);
    }

    @Override
    public List<ChatMessage> getHistory(String channel) {
        return Optional.ofNullable(chatHistoryStore.get(channel))
            .stream()
            .flatMap(Collection::stream)
            .toList();
    }
}
