package com.example.order_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "check_in_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckInLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    private Long eventId; // Redundant but useful for quick queries
    @Column(columnDefinition = "BINARY(16)")
    private UUID userId;  // Redundant but useful for quick queries
    private LocalDateTime checkInTime;
    private String gate; // e.g., "Main Entrance", "VIP Gate"
    private String deviceId; // ID of the scanning device

    @PrePersist
    public void onCreate() {
        checkInTime = LocalDateTime.now();
    }
}
