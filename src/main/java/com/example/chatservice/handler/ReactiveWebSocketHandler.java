package com.example.chatservice.handler;

import com.example.chatservice.command.CommandProcessor;
import com.example.chatservice.dto.ChatCommand;
import com.example.chatservice.dto.ErrorMessageDto;
import com.example.chatservice.exception.UnknownCommandException;
import com.example.chatservice.service.ChannelService;
import com.example.chatservice.service.SessionService;
import com.example.chatservice.util.ObjectMapperUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ReactiveWebSocketHandler implements WebSocketHandler {

    private static final ConcurrentMap<String, Sinks.Many<WebSocketMessage>> activeSessionIdMap = new ConcurrentHashMap<>();

    private final SessionService sessionService;
    private final ChannelService channelService;
    private final ObjectMapperUtil objectMapper;
    private final Map<ChatCommand, CommandProcessor> commandCommandProcessorMap;

    public ReactiveWebSocketHandler(SessionService sessionService,
                                    ChannelService channelService,
                                    ObjectMapperUtil objectMapper,
                                    List<CommandProcessor> commandProcessors) {
        this.sessionService = sessionService;
        this.channelService = channelService;
        this.objectMapper = objectMapper;
        this.commandCommandProcessorMap = commandProcessors
            .stream()
            .collect(Collectors.toMap(CommandProcessor::chatCommand, Function.identity()));
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        final String sessionId = session.getId();
        if (!activeSessionIdMap.containsKey(sessionId)) {
            var outgoingMessages = Sinks.many().multicast().<WebSocketMessage>onBackpressureBuffer();
            activeSessionIdMap.put(sessionId, outgoingMessages);
            sessionService.createSession(sessionId);
            Mono<Void> inbound = buildMessageProcessor(session, sessionId, outgoingMessages);
            var outbound = session.send(outgoingMessages.asFlux());
            return Mono.zip(inbound, outbound).then();
        } else {
            return Mono.empty();
        }
    }

    private Mono<Void> buildMessageProcessor(WebSocketSession session, String sessionId, Sinks.Many<WebSocketMessage> sink) {
        return session.receive()
            .map(WebSocketMessage::getPayloadAsText)
            .map(String::trim)
            .mapNotNull(messagePayload -> {
                try {
                    var command = ChatCommand.findCommand(messagePayload);
                    var commandProcessor = commandCommandProcessorMap.get(command);
                    if (commandProcessor == null) {
                        throw new UnknownCommandException(command);
                    }
                    return switch (command) {
                        case JOIN, LIST, LOGIN, USERS, LEAVE -> commandProcessor.process(sessionId, messagePayload);
                        case DISCONNECT -> processDisconnect(session, sessionId, messagePayload, commandProcessor);
                        case OTHER -> {
                            processOtherCommandType(session, sessionId, messagePayload, commandProcessor);
                            yield null;
                        }
                    };
                } catch (Exception ex) {
                    log.error("Something went wrong:", ex);
                    return objectMapper.writeValueAsString(ErrorMessageDto.builder()
                        .timestamp(Instant.now().toEpochMilli())
                        .errorMessage(ex.getMessage())
                        .build());
                }
            })
            .filter(Objects::nonNull)
            .map(str -> {
                sink.tryEmitNext(session.textMessage(str));
                return str;
            })
            .map(session::textMessage)
            .doFinally(sig -> {
                log.info("Terminating session: sessionId = {}", sessionId);
                activeSessionIdMap.remove(sessionId);
            })
            .then();
    }

    private void processOtherCommandType(WebSocketSession session,
                                         String sessionId,
                                         String messagePayload,
                                         CommandProcessor commandProcessor) {
        if (messagePayload.isBlank()) {
            return;
        }
        var chatMsg = commandProcessor.process(sessionId, messagePayload);
        if (chatMsg != null && !chatMsg.isBlank()) {
            var sessionDetails = sessionService.getSessionDetails(sessionId);
            sessionDetails
                .flatMap(sd -> channelService.getCurrentUser(sd.getUsername()))
                .stream()
                .flatMap(user -> channelService.getUsers(user.getChannel()).stream())
                .flatMap(u -> sessionService.getActiveSessions(u.getUsername())
                    .stream()
                    .filter(s -> !s.getSessionId().equals(sessionId)))
                .forEach(s -> activeSessionIdMap.get(s.getSessionId()).tryEmitNext(session.textMessage(chatMsg)));
        }
    }

    private String processDisconnect(WebSocketSession session,
                                     String sessionId,
                                     String messagePayload,
                                     CommandProcessor commandProcessor) {
        var result = commandProcessor.process(sessionId, messagePayload);
        activeSessionIdMap.remove(session.getId());
        session.close(CloseStatus.NORMAL).subscribe();
        return result;
    }
}
