package com.example.order_service.service;

import com.example.order_service.model.Order;
import com.example.order_service.model.Ticket;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportingService {

    private final OrderRepository orderRepository;
    private final TicketRepository ticketRepository;

    public BigDecimal calculateTotalRevenue() {
        return orderRepository.findAll().stream()
                .filter(order -> order.getStatus() == Order.OrderStatus.PAID)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateRevenueForEvent(Long eventId) {
        return orderRepository.findByEventId(eventId).stream()
                .filter(order -> order.getStatus() == Order.OrderStatus.PAID)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getTotalTicketsSold() {
        return ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getStatus() == Ticket.TicketStatus.ISSUED ||
                                   ticket.getStatus() == Ticket.TicketStatus.SCANNED ||
                                   ticket.getStatus() == Ticket.TicketStatus.TRANSFERRED)
                .count();
    }

    public Long getTicketsSoldForEvent(Long eventId) {
        return ticketRepository.findByOrderItem_Order_EventId(eventId).stream()
                .filter(ticket -> ticket.getStatus() == Ticket.TicketStatus.ISSUED ||
                                   ticket.getStatus() == Ticket.TicketStatus.SCANNED ||
                                   ticket.getStatus() == Ticket.TicketStatus.TRANSFERRED)
                .count();
    }

    // You can add more sophisticated reporting methods here, e.g.:
    // - Revenue over time (daily, weekly, monthly)
    // - Sales by category
    // - Peak sales times
    // - Export to CSV (this would involve creating a CSV generation utility)
}
