package com.example.ticket_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventDetailsDto {
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
