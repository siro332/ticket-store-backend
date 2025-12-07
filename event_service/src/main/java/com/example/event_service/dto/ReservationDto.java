package com.example.event_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDto {
    private Long id;
    private Long userId;
    private Long eventId;
    private Long ticketTypeId;
    private Long seatId;
    private Integer quantity;
    private LocalDateTime expireAt;
    private String status; // ReservationStatus enum as String
}
