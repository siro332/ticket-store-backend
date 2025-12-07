package com.example.order_service.controller;

import com.example.order_service.dto.OrderResponse.TicketResponse;
import com.example.order_service.model.Ticket;
import com.example.order_service.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @GetMapping("/{ticketCode}")
    public ResponseEntity<TicketResponse> getTicketByCode(@PathVariable String ticketCode) {
        Ticket ticket = ticketService.getTicketByCode(ticketCode);
        return ResponseEntity.ok(TicketResponse.fromEntity(ticket));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TicketResponse>> getTicketsForUser(@PathVariable Long userId) {
        List<Ticket> tickets = ticketService.getTicketsForUser(userId);
        return ResponseEntity.ok(tickets.stream().map(TicketResponse::fromEntity).collect(Collectors.toList()));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<TicketResponse>> getTicketsForEvent(@PathVariable Long eventId) {
        List<Ticket> tickets = ticketService.getTicketsForEvent(eventId);
        return ResponseEntity.ok(tickets.stream().map(TicketResponse::fromEntity).collect(Collectors.toList()));
    }

    @PostMapping("/{ticketCode}/scan")
    public ResponseEntity<TicketResponse> scanTicket(@PathVariable String ticketCode) {
        Ticket scannedTicket = ticketService.scanTicket(ticketCode);
        return ResponseEntity.ok(TicketResponse.fromEntity(scannedTicket));
    }

    @PostMapping("/{ticketCode}/transfer")
    public ResponseEntity<TicketResponse> transferTicket(
            @PathVariable String ticketCode,
            @RequestParam String newAttendeeName,
            @RequestParam String newAttendeeEmail) {
        Ticket transferredTicket = ticketService.transferTicket(ticketCode, newAttendeeName, newAttendeeEmail);
        return ResponseEntity.ok(TicketResponse.fromEntity(transferredTicket));
    }
}
