package com.example.chatservice.command;

import com.example.chatservice.dto.ChatCommand;
import com.example.chatservice.service.ChannelService;
import com.example.chatservice.service.MessageService;
import com.example.chatservice.util.AuthUtil;
import com.example.chatservice.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.example.chatservice.dto.ChatCommand.OTHER;

@Component
@Slf4j
public class OtherCommandProcessor implements CommandProcessor {
    private final AuthUtil authUtil;
    private final ChannelService channelService;
    private final MessageService messageService;
    private final ObjectMapperUtil objectMapper;

    public OtherCommandProcessor(AuthUtil authUtil,
                                 ChannelService channelService,
                                 MessageService messageService,
                                 ObjectMapperUtil objectMapper) {
        this.authUtil = authUtil;
        this.channelService = channelService;
        this.messageService = messageService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ChatCommand chatCommand() {
        return OTHER;
    }

    @Override
    public String process(String sessionId, String commandPayload) {
        var sessionDetails = authUtil.checkCurrentSession(sessionId);
        if (!commandPayload.endsWith("CR") || commandPayload.length() == 2) {
            return null;
        }
        var channelUserOpt = channelService.getCurrentUser(sessionDetails.getUsername());
        return channelUserOpt
            .map(channelUser -> {
                var messageToSend = commandPayload.substring(0, commandPayload.length() - 2);
                var chatMessage = messageService.sendMessage(channelUser.getChannel(), messageToSend, sessionDetails);
                return objectMapper.writeValueAsString(chatMessage);
            })
            .orElse(null);
    }
}
