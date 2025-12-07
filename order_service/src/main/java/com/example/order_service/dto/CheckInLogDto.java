package com.example.order_service.dto;

import com.example.order_service.model.CheckInLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckInLogDto {
    private Long id;
    private Long eventId;
    private UUID userId;
    private LocalDateTime checkInTime;
    private String gate;
    private String deviceId;
    private TicketInfoDto ticket; // DTO for ticket information

    public static CheckInLogDto fromEntity(CheckInLog checkInLog) {
        return CheckInLogDto.builder()
                .id(checkInLog.getId())
                .eventId(checkInLog.getEventId())
                .userId(checkInLog.getUserId())
                .checkInTime(checkInLog.getCheckInTime())
                .gate(checkInLog.getGate())
                .deviceId(checkInLog.getDeviceId())
                .ticket(TicketInfoDto.fromEntity(checkInLog.getTicket())) // Convert Ticket to TicketInfoDto
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketInfoDto {
        private Long id;
        private String ticketCode;
        private String attendeeName;
        private String attendeeEmail;
        private String status;

        public static TicketInfoDto fromEntity(com.example.order_service.model.Ticket ticket) {
            if (ticket == null) return null;
            return TicketInfoDto.builder()
                    .id(ticket.getId())
                    .ticketCode(ticket.getTicketCode())
                    .attendeeName(ticket.getAttendeeName())
                    .attendeeEmail(ticket.getAttendeeEmail())
                    .status(ticket.getStatus().name())
                    .build();
        }
    }
}
