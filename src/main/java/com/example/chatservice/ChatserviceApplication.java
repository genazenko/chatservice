package com.example.chatservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class ChatserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatserviceApplication.class, args);
    }
}
