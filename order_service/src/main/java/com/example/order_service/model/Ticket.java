package com.example.order_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    private Long seatId; // Link to specific seat if applicable
    private String ticketCode; // Unique code for QR/PDF
    private String attendeeName;
    private String attendeeEmail;

    @Enumerated(EnumType.STRING)
    private TicketStatus status; // ISSUED, SCANNED, REFUNDED, TRANSFERRED

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        if (status == null) status = TicketStatus.ISSUED;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum TicketStatus {
        ISSUED, SCANNED, REFUNDED, TRANSFERRED
    }
}
