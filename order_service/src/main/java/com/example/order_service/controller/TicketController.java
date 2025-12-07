package com.example.order_service.controller;

import com.example.order_service.dto.CheckInLogDto;
import com.example.order_service.dto.OrderResponse;
import com.example.order_service.model.Ticket;
import com.example.order_service.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @GetMapping("/{ticketCode}")
    public ResponseEntity<OrderResponse.TicketResponse> getTicketByCode(@PathVariable String ticketCode) {
        Ticket ticket = ticketService.getTicketByCode(ticketCode);
        return ResponseEntity.ok(OrderResponse.TicketResponse.fromEntity(ticket));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse.TicketResponse>> getTicketsForUser(@PathVariable UUID userId) {
        List<Ticket> tickets = ticketService.getTicketsForUser(userId);
        return ResponseEntity.ok(tickets.stream().map(OrderResponse.TicketResponse::fromEntity).collect(Collectors.toList()));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<OrderResponse.TicketResponse>> getTicketsForEvent(@PathVariable Long eventId) {
        List<Ticket> tickets = ticketService.getTicketsForEvent(eventId);
        return ResponseEntity.ok(tickets.stream().map(OrderResponse.TicketResponse::fromEntity).collect(Collectors.toList()));
    }

    @PostMapping("/{ticketCode}/scan")
    public ResponseEntity<OrderResponse.TicketResponse> scanTicket(
            @PathVariable String ticketCode,
            @RequestParam(required = false) String gate,
            @RequestParam(required = false) String deviceId) {
        Ticket scannedTicket = ticketService.scanTicket(ticketCode, gate, deviceId);
        return ResponseEntity.ok(OrderResponse.TicketResponse.fromEntity(scannedTicket));
    }

    @PostMapping("/{ticketCode}/transfer")
    public ResponseEntity<OrderResponse.TicketResponse> transferTicket(
            @PathVariable String ticketCode,
            @RequestParam String newAttendeeName,
            @RequestParam String newAttendeeEmail) {
        Ticket transferredTicket = ticketService.transferTicket(ticketCode, newAttendeeName, newAttendeeEmail);
        return ResponseEntity.ok(OrderResponse.TicketResponse.fromEntity(transferredTicket));
    }

    @GetMapping("/event/{eventId}/check-in-logs")
    public ResponseEntity<List<CheckInLogDto>> getCheckInLogsForEvent(@PathVariable Long eventId) {
        List<CheckInLogDto> checkInLogs = ticketService.getCheckInLogsForEvent(eventId);
        return ResponseEntity.ok(checkInLogs);
    }
}
