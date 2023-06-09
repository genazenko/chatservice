package com.example.chatservice.command;

import com.example.chatservice.dto.ChatCommand;

public interface CommandProcessor {
    ChatCommand chatCommand();

    /**
     * @param sessionId
     * @param commandPayload
     * @return The message that we want to send to the user, otherwise it will be null
     */
    String process(String sessionId, String commandPayload);
}
