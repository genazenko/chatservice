package com.example.chatservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorMessageDto {
    private String originalCommand;
    private String errorMessage;
    private Long timestamp;
}
