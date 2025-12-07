package com.example.order_service.service;

import com.example.order_service.model.Ticket;
import com.example.order_service.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public Ticket getTicketByCode(String ticketCode) {
        return ticketRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> new RuntimeException("Ticket not found with code: " + ticketCode));
    }

    public List<Ticket> getTicketsForUser(Long userId) {
        return ticketRepository.findByOrderItem_Order_UserId(userId);
    }

    public List<Ticket> getTicketsForEvent(Long eventId) {
        return ticketRepository.findByOrderItem_Order_EventId(eventId);
    }

    @Transactional
    public Ticket scanTicket(String ticketCode) {
        Ticket ticket = getTicketByCode(ticketCode);
        if (ticket.getStatus() == Ticket.TicketStatus.ISSUED || ticket.getStatus() == Ticket.TicketStatus.TRANSFERRED) {
            ticket.setStatus(Ticket.TicketStatus.SCANNED);
            return ticketRepository.save(ticket);
        } else {
            throw new RuntimeException("Ticket " + ticketCode + " cannot be scanned. Current status: " + ticket.getStatus());
        }
    }

    @Transactional
    public Ticket transferTicket(String ticketCode, String newAttendeeName, String newAttendeeEmail) {
        Ticket ticket = getTicketByCode(ticketCode);
        if (ticket.getStatus() == Ticket.TicketStatus.ISSUED || ticket.getStatus() == Ticket.TicketStatus.SCANNED) {
            ticket.setAttendeeName(newAttendeeName);
            ticket.setAttendeeEmail(newAttendeeEmail);
            ticket.setStatus(Ticket.TicketStatus.TRANSFERRED);
            return ticketRepository.save(ticket);
        } else {
            throw new RuntimeException("Ticket " + ticketCode + " cannot be transferred. Current status: " + ticket.getStatus());
        }
    }

    @Transactional
    public Ticket updateTicketStatus(String ticketCode, Ticket.TicketStatus newStatus) {
        Ticket ticket = getTicketByCode(ticketCode);
        ticket.setStatus(newStatus);
        return ticketRepository.save(ticket);
    }
}
