package com.example.chatservice.service;

import com.example.chatservice.entity.ChannelUser;
import com.example.chatservice.repository.ChannelUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ChannelService {
    private final ChannelUserRepository channelUserRepository;

    public ChannelService(ChannelUserRepository channelUserRepository) {
        this.channelUserRepository = channelUserRepository;
    }

    public void joinChannel(String username, String channelName) {
        channelUserRepository.save(ChannelUser.builder()
            .channel(channelName)
            .isActive(true)
            .username(username)
            .build());
    }

    public void leaveChannel(String username) {
        log.info("Leaving current channel for username = {}", username);
        channelUserRepository.markChannelUserInactive(username);
    }

    public List<ChannelUser> getUsers(String channel) {
        return channelUserRepository.getByChannelAndIsActive(channel, true);
    }

    public List<String> getChannels() {
        return channelUserRepository.getChannels();
    }

    public Optional<String> getLatestChannel(String username) {
        return channelUserRepository.getByUsername(username).map(ChannelUser::getChannel);
    }

    public Optional<ChannelUser> getCurrentUser(String username) {
        return channelUserRepository.getByUsername(username);
    }
}
