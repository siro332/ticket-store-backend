package com.example.order_service.service;

import com.example.order_service.dto.CheckInLogDto;
import com.example.order_service.dto.EventDto;
import com.example.order_service.feign_client.EventServiceClient;
import com.example.order_service.model.CheckInLog;
import com.example.order_service.model.Ticket;
import com.example.order_service.model.Ticket.TicketStatus;
import com.example.order_service.repository.CheckInLogRepository;
import com.example.order_service.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CheckInLogRepository checkInLogRepository;
    private final EventServiceClient eventServiceClient;

    public Ticket getTicketByCode(String ticketCode) {
        return ticketRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> new RuntimeException("Ticket not found with code: " + ticketCode));
    }

    public List<Ticket> getTicketsForUser(UUID userId) {
        return ticketRepository.findByOrderItem_Order_UserId(userId);
    }

    public List<Ticket> getTicketsForEvent(Long eventId) {
        return ticketRepository.findByOrderItem_Order_EventId(eventId);
    }

    @Transactional
    public Ticket scanTicket(String ticketCode, String gate, String deviceId) {
        Ticket ticket = getTicketByCode(ticketCode);
        if (ticket.getStatus() == TicketStatus.SCANNED) {
            throw new RuntimeException("Ticket " + ticketCode + " has already been scanned.");
        } else if (ticket.getStatus() == TicketStatus.ISSUED || ticket.getStatus() == TicketStatus.TRANSFERRED) {
            ticket.setStatus(TicketStatus.SCANNED);
            Ticket savedTicket = ticketRepository.save(ticket);

            // Create and save CheckInLog
            CheckInLog checkInLog = CheckInLog.builder()
                    .ticket(savedTicket)
                    .eventId(savedTicket.getOrderItem().getOrder().getEventId())
                    .userId(savedTicket.getOrderItem().getOrder().getUserId())
                    .checkInTime(LocalDateTime.now())
                    .gate(gate)
                    .deviceId(deviceId)
                    .build();
            checkInLogRepository.save(checkInLog);

            return savedTicket;
        } else {
            throw new RuntimeException("Ticket " + ticketCode + " cannot be scanned. Current status: " + ticket.getStatus());
        }
    }

    @Transactional
    public Ticket transferTicket(String ticketCode, String newAttendeeName, String newAttendeeEmail) {
        Ticket ticket = getTicketByCode(ticketCode);

        // Validate with EventService if transfer is allowed
        Long eventId = ticket.getOrderItem().getOrder().getEventId();
        EventDto event = eventServiceClient.getEventById(eventId);
        if (Boolean.FALSE.equals(event.getAllowTicketTransfer())) {
            throw new RuntimeException("Ticket transfer is not allowed for this event.");
        }

        if (ticket.getStatus() == TicketStatus.ISSUED || ticket.getStatus() == TicketStatus.SCANNED) {
            ticket.setAttendeeName(newAttendeeName);
            ticket.setAttendeeEmail(newAttendeeEmail);
            ticket.setStatus(TicketStatus.TRANSFERRED);
            return ticketRepository.save(ticket);
        } else {
            throw new RuntimeException("Ticket " + ticketCode + " cannot be transferred. Current status: " + ticket.getStatus());
        }
    }

    @Transactional
    public Ticket updateTicketStatus(String ticketCode, TicketStatus newStatus) {
        Ticket ticket = getTicketByCode(ticketCode);
        ticket.setStatus(newStatus);
        return ticketRepository.save(ticket);
    }

    public List<CheckInLogDto> getCheckInLogsForEvent(Long eventId) {
        return checkInLogRepository.findByEventId(eventId).stream()
                .map(CheckInLogDto::fromEntity)
                .collect(Collectors.toList());
    }
}