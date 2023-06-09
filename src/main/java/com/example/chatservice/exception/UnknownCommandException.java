package com.example.chatservice.exception;

import com.example.chatservice.dto.ChatCommand;

public class UnknownCommandException extends RuntimeException {
    public UnknownCommandException(ChatCommand chatCommand) {
        super("Can't process command = " + chatCommand + ", commandProcessor not found.");
    }

    public UnknownCommandException(String chatCommand) {
        super("Can't process command = " + chatCommand);
    }
}
