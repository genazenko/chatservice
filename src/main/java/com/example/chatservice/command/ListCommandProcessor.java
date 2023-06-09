package com.example.chatservice.command;

import com.example.chatservice.dto.ChatCommand;
import com.example.chatservice.service.ChannelService;
import com.example.chatservice.util.AuthUtil;
import com.example.chatservice.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ListCommandProcessor implements CommandProcessor {
    private final AuthUtil authUtil;
    private final ChannelService channelService;
    private final ObjectMapperUtil objectMapperUtil;

    public ListCommandProcessor(AuthUtil authUtil,
                                ChannelService channelService,
                                ObjectMapperUtil objectMapperUtil) {
        this.authUtil = authUtil;
        this.channelService = channelService;
        this.objectMapperUtil = objectMapperUtil;
    }

    @Override
    public ChatCommand chatCommand() {
        return ChatCommand.LIST;
    }

    @Override
    public String process(String sessionId, String commandPayload) {
        authUtil.checkCurrentSession(sessionId);
        return objectMapperUtil.writeValueAsString(channelService.getChannels());
    }
}
