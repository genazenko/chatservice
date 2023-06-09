package com.example.chatservice.repository;

import com.example.chatservice.entity.ChannelUser;
import com.example.chatservice.exception.ChannelLimitException;
import com.example.chatservice.repository.impl.InMemoryChannelUserRepository;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ChannelUserRepositoryTest {
    private final Integer channelLimit = 10;
    private ChannelUserRepository channelUserRepository;

    @Test
    void saveChannelUser() {
        channelUserRepository = new InMemoryChannelUserRepository(channelLimit);
        var channelUser = ChannelUser.builder().channel("channel").username("user").isActive(true).build();
        channelUserRepository.save(channelUser);
        var chUser = channelUserRepository.getByUsername(channelUser.getUsername());
        assertTrue(chUser.isPresent());
        assertEquals(channelUser.getUsername(), chUser.get().getUsername());
        assertEquals(channelUser.getChannel(), chUser.get().getChannel());
        assertEquals(channelUser.getIsActive(), chUser.get().getIsActive());
    }

    @Test
    void channelLimitExceeded() {
        channelUserRepository = new InMemoryChannelUserRepository(channelLimit);
        IntStream.range(0, 10).forEach(i -> {
            var channelUser = ChannelUser.builder().channel("channel").username("user" + i).isActive(true).build();
            channelUserRepository.save(channelUser);
        });
        assertThrows(ChannelLimitException.class, () -> channelUserRepository.save(ChannelUser
            .builder()
            .channel("channel")
            .username("oneMoreUser")
            .isActive(true)
            .build()));
    }

    @Test
    void listChannels() {
        channelUserRepository = new InMemoryChannelUserRepository(channelLimit);
        IntStream.range(0, 10).forEach(i -> {
            var channelUser = ChannelUser.builder().channel("channel" + i).username("user" + i).isActive(true).build();
            channelUserRepository.save(channelUser);
        });
        var channels = channelUserRepository.getChannels();

        assertEquals(10, channels.size());
    }

    @Test
    void markInactiveTest() {
        channelUserRepository = new InMemoryChannelUserRepository(channelLimit);
        var channelUser = ChannelUser.builder().channel("channel").username("user").isActive(true).build();
        channelUserRepository.save(channelUser);
        channelUserRepository.markChannelUserInactive(channelUser.getUsername());

        var chUser = channelUserRepository.getByUsername(channelUser.getUsername());
        assertTrue(chUser.isPresent());
        assertFalse(chUser.get().getIsActive());
    }
}
