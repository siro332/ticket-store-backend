package com.example.order_service.service;

import com.example.order_service.model.Order;
import com.example.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportingService {
    private final OrderRepository orderRepository;

    public BigDecimal getRevenueByEvent(Long eventId) {
        List<Order> orders = orderRepository.findByEventIdAndStatus(eventId, Order.OrderStatus.PAID);
        return orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long getTicketsSoldByEvent(Long eventId) {
        // This logic needs to be re-implemented.
        // It should probably fetch this information from the ticket_service.
        return 0;
    }

    public BigDecimal calculateTotalRevenue() {
        List<Order> orders = orderRepository.findByStatus(Order.OrderStatus.PAID);
        return orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long getTotalTicketsSold() {
        // This logic needs to be re-implemented.
        // It should probably fetch this information from the ticket_service.
        return 0;
    }
}
