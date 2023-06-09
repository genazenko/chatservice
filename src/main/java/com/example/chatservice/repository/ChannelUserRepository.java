package com.example.chatservice.repository;

import com.example.chatservice.entity.ChannelUser;

import java.util.List;
import java.util.Optional;

public interface ChannelUserRepository {
    void save(ChannelUser channelUser);

    Optional<ChannelUser> getByUsername(String username);

    void markChannelUserInactive(String username);

    List<ChannelUser> getByChannelAndIsActive(String channel, Boolean isActive);

    List<String> getChannels();
}
