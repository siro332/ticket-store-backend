package com.example.order_service.repository;

import com.example.order_service.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByOrderItemId(Long orderItemId);
    Optional<Ticket> findByTicketCode(String ticketCode);
    List<Ticket> findByOrderItem_Order_UserId(UUID userId);
    List<Ticket> findByOrderItem_Order_EventId(Long eventId);
    Long countByOrderItem_Order_UserIdAndOrderItem_TicketTypeId(UUID userId, Long ticketTypeId);
}
