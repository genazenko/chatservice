package com.example.chatservice.command;

import com.example.chatservice.dto.ChatCommand;
import com.example.chatservice.service.ChannelService;
import com.example.chatservice.service.MessageService;
import com.example.chatservice.util.AuthUtil;
import com.example.chatservice.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JoinCommandProcessor implements CommandProcessor {

    private final AuthUtil authUtil;
    private final ChannelService channelService;
    private final MessageService messageService;
    private final ObjectMapperUtil objectMapper;

    public JoinCommandProcessor(AuthUtil authUtil,
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
        return ChatCommand.JOIN;
    }

    @Override
    public String process(String sessionId, String commandPayload) {
        var sessionDetails = authUtil.checkCurrentSession(sessionId);
        var channelName = commandPayload.replace(ChatCommand.JOIN.prefix, "").trim();
        if (channelName.isBlank()) {
            throw new IllegalArgumentException("Please insert channel name after /join command");
        }

        log.info("Joining to the channel = {}, username = {}", channelName, sessionDetails.getUsername());
        channelService.joinChannel(sessionDetails.getUsername(), channelName);
        return objectMapper.writeValueAsString(messageService.getHistory(channelName));
    }
}
