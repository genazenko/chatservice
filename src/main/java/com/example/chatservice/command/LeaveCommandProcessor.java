package com.example.chatservice.command;

import com.example.chatservice.dto.ChatCommand;
import com.example.chatservice.service.ChannelService;
import com.example.chatservice.util.AuthUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LeaveCommandProcessor implements CommandProcessor {
    private final AuthUtil authUtil;
    private final ChannelService channelService;

    public LeaveCommandProcessor(AuthUtil authUtil, ChannelService channelService) {
        this.authUtil = authUtil;
        this.channelService = channelService;
    }

    @Override
    public ChatCommand chatCommand() {
        return ChatCommand.LEAVE;
    }

    @Override
    public String process(String sessionId, String commandPayload) {
        var sessionDetails = authUtil.checkCurrentSession(sessionId);
        channelService.leaveChannel(sessionDetails.getUsername());
        return null;
    }
}
