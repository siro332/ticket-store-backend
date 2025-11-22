package com.example.payment_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private String gateway;
    private String transactionId;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Status {
        PENDING,
        SUCCESS,
        FAILED
    }

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        if (status == null) status = Status.PENDING;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
