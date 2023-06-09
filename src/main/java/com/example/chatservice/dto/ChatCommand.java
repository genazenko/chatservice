package com.example.chatservice.dto;

import com.example.chatservice.exception.UnknownCommandException;

import java.util.Arrays;

public enum ChatCommand {
    LOGIN("/login"),
    JOIN("/join"),
    LEAVE("/leave"),
    DISCONNECT("/disconnect"),
    LIST("/list"),
    USERS("/users"),
    OTHER(null);

    public final String prefix;

    ChatCommand(String prefix) {
        this.prefix = prefix;
    }

    public static ChatCommand findCommand(String payload) {
        if (payload == null || !payload.startsWith("/")) {
            return OTHER;
        }
        var commandPrefix = payload.split(" ")[0];
        return Arrays.stream(values())
            .filter(command -> command.prefix != null)
            .filter(command -> command.prefix.equalsIgnoreCase(commandPrefix))
            .findFirst()
            .orElseThrow(() -> new UnknownCommandException(commandPrefix));
    }
}
