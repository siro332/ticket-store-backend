package com.example.event_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "BINARY(16)")
    private UUID organizerId;  // liên kết với AuthService (User/Organization)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;

    private String coverImage;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Builder.Default
    private Boolean allowTicketTransfer = false; // New field
    @Builder.Default
    private Boolean allowAttendeeNameChange = false; // New field
    
    @Builder.Default
    private Boolean refundEnabled = false;
    @Builder.Default
    private Integer refundDeadlineHours = 24; // Default 24 hours before event
    @Builder.Default
    private Double refundFeePercent = 0.0;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (status == null) status = Status.DRAFT;
        if (allowTicketTransfer == null) allowTicketTransfer = false;
        if (allowAttendeeNameChange == null) allowAttendeeNameChange = false;
        if (refundEnabled == null) refundEnabled = false;
        if (refundDeadlineHours == null) refundDeadlineHours = 24;
        if (refundFeePercent == null) refundFeePercent = 0.0;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum Status {
        DRAFT, PUBLISHED, CANCELLED
    }
}
