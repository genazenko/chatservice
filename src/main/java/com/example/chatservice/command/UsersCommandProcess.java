package com.example.chatservice.command;

import com.example.chatservice.dto.ChatCommand;
import com.example.chatservice.service.ChannelService;
import com.example.chatservice.util.AuthUtil;
import com.example.chatservice.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UsersCommandProcess implements CommandProcessor {
    private final AuthUtil authUtil;
    private final ChannelService channelService;
    private final ObjectMapperUtil objectMapper;

    public UsersCommandProcess(AuthUtil authUtil,
                               ChannelService channelService,
                               ObjectMapperUtil objectMapper) {
        this.authUtil = authUtil;
        this.channelService = channelService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ChatCommand chatCommand() {
        return ChatCommand.USERS;
    }

    @Override
    public String process(String sessionId, String commandPayload) {
        var sessionDetails = authUtil.checkCurrentSession(sessionId);
        var channelUserOpt = channelService.getCurrentUser(sessionDetails.getUsername());
        return channelUserOpt
            .map(channelUser -> channelService.getUsers(channelUser.getChannel()))
            .map(objectMapper::writeValueAsString)
            .orElse(null);
    }
}
