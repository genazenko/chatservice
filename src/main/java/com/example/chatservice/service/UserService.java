package com.example.chatservice.service;

import com.example.chatservice.dto.LoginDto;
import com.example.chatservice.entity.User;
import com.example.chatservice.exception.AuthorizationException;
import com.example.chatservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@Slf4j
public class UserService {
    private static final String AUTH_ERROR_MESSAGE = "Incorrect username or password";
    private static final String ALREADY_AUTHORIZED = "User already authorized";

    private final UserRepository userRepository;
    private final PasswordEncryptionService encryptionService;
    private final SessionService sessionService;

    public UserService(UserRepository userRepository,
                       PasswordEncryptionService encryptionService,
                       SessionService sessionService) {
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
        this.sessionService = sessionService;
    }

    public void login(LoginDto loginDto, String sessionId) {
        log.info("Starting login process");
        var sessionDetails = sessionService.getSessionDetails(sessionId);
        sessionDetails.ifPresent(session -> {
            if (session.getIsAuthorized()) {
                log.error("Session is already authorized.");
                throw new AuthorizationException(ALREADY_AUTHORIZED);
            }
        });
        var currentUser = userRepository.getByUsername(loginDto.getUsername());
        currentUser.ifPresentOrElse(user -> {
            log.info("Checking user credentials");
            if (!Arrays.equals(encryptionService.encryptPassword(loginDto.getPassword()), user.getHashedPassword())) {
                throw new AuthorizationException(AUTH_ERROR_MESSAGE);
            }
        }, () -> {
            log.info("Creating a new user");
            userRepository.save(createUser(loginDto));
        });

        sessionService.updateSessionWithUsername(sessionId, loginDto.getUsername());
        log.info("Successfully logged in");
    }

    private User createUser(LoginDto loginDto) {
        return User.builder()
            .username(loginDto.getUsername())
            .hashedPassword(encryptionService.encryptPassword(loginDto.getPassword()))
            .build();
    }
}
