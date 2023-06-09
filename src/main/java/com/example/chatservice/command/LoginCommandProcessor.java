package com.example.chatservice.command;

import com.example.chatservice.dto.ChatCommand;
import com.example.chatservice.dto.LoginDto;
import com.example.chatservice.service.ChannelService;
import com.example.chatservice.service.UserService;
import com.example.chatservice.util.AuthUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoginCommandProcessor implements CommandProcessor {
    private final UserService userService;
    private final AuthUtil authUtil;
    private final ChannelService channelService;
    private final JoinCommandProcessor joinCommandProcessor;

    public LoginCommandProcessor(UserService userService,
                                 AuthUtil authUtil,
                                 ChannelService channelService,
                                 JoinCommandProcessor joinCommandProcessor) {
        this.userService = userService;
        this.authUtil = authUtil;
        this.channelService = channelService;
        this.joinCommandProcessor = joinCommandProcessor;
    }

    @Override
    public ChatCommand chatCommand() {
        return ChatCommand.LOGIN;
    }

    @Override
    public String process(String sessionId, String commandPayload) {
        var payloadParts = commandPayload.split(" ");
        if (payloadParts.length != 3) {
            throw new IllegalArgumentException("Please provide username and password to the /login command (/login <name> <password>)");
        }
        var loginDto = LoginDto.builder().username(payloadParts[1].trim()).password(payloadParts[2].trim()).build();
        userService.login(loginDto, sessionId);
        var sessionDetails = authUtil.checkCurrentSession(sessionId);
        var latestChannel = channelService.getLatestChannel(sessionDetails.getUsername());
        return latestChannel.map(channel ->
                joinCommandProcessor.process(sessionDetails.getSessionId(), "/join " + channel))
            .orElse(null);
    }
}
