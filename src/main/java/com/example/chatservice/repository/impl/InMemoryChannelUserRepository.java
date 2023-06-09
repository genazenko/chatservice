package com.example.chatservice.repository.impl;

import com.example.chatservice.entity.ChannelUser;
import com.example.chatservice.exception.ChannelLimitException;
import com.example.chatservice.repository.ChannelUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class InMemoryChannelUserRepository implements ChannelUserRepository {
    private final ConcurrentMap<String, ChannelUser> channelUserStore = new ConcurrentHashMap<>();
    private final Integer channelLimit;

    public InMemoryChannelUserRepository(@Value("${channel.limit}") Integer channelLimit) {
        this.channelLimit = channelLimit;
    }

    @Override
    public synchronized void save(ChannelUser channelUser) {
        var currentUser = channelUserStore.get(channelUser.getUsername());
        if (currentUser != null && currentUser.equals(channelUser)) {
            return;
        }
        long size = channelUserStore.values()
            .stream()
            .filter(ChannelUser::getIsActive)
            .filter(user -> user.getChannel().equals(channelUser.getChannel()))
            .count();
        if (size >= channelLimit) {
            throw new ChannelLimitException();
        }
        channelUserStore.put(channelUser.getUsername(), channelUser);
    }


    @Override
    public Optional<ChannelUser> getByUsername(String username) {
        return Optional.ofNullable(channelUserStore.get(username));
    }

    @Override
    public synchronized void markChannelUserInactive(String username) {
        var user = channelUserStore.get(username);
        if (user != null) {
            user.setIsActive(false);
            channelUserStore.put(username, user);
        }
    }

    @Override
    public List<ChannelUser> getByChannelAndIsActive(String channel, Boolean isActive) {
        return channelUserStore.values()
            .stream()
            .filter(user -> user.getChannel().equals(channel))
            .filter(user -> user.getIsActive().equals(isActive))
            .toList();
    }

    @Override
    public List<String> getChannels() {
        return channelUserStore.values().stream().map(ChannelUser::getChannel).distinct().toList();
    }
}
