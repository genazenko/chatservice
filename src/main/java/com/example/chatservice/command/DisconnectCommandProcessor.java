package com.example.chatservice.command;

import com.example.chatservice.dto.ChatCommand;
import com.example.chatservice.service.ChannelService;
import com.example.chatservice.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DisconnectCommandProcessor implements CommandProcessor {
    private final SessionService sessionService;
    private final ChannelService channelService;

    public DisconnectCommandProcessor(SessionService sessionService, ChannelService channelService) {
        this.sessionService = sessionService;
        this.channelService = channelService;
    }

    @Override
    public ChatCommand chatCommand() {
        return ChatCommand.DISCONNECT;
    }

    @Override
    public String process(String sessionId, String commandPayload) {
        log.info("Terminating session with ID = {}", sessionId);
        sessionService.terminateSession(sessionId);
        var sessionDetails = sessionService.getSessionDetails(sessionId);
        sessionDetails
            .filter(details -> details.getUsername() != null)
            .ifPresent(details -> {
                var otherActive = sessionService.getActiveSessions(details.getUsername());
                if (otherActive.isEmpty()) {
                    channelService.leaveChannel(details.getUsername());
                }
            });
        return null;
    }
}
