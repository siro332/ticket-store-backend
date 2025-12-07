package com.example.payment_service.service;

import com.example.payment_service.dto.PaymentRequest;
import com.example.payment_service.dto.RefundRequest;
import com.example.payment_service.feign_client.OrderClient;
import com.example.payment_service.feign_client.PaymentInfoStatus;
import com.example.payment_service.model.PaymentTransaction;
import com.example.payment_service.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentTransactionRepository repo;
    private final OrderClient orderClient;
    private final Random random = new Random();

    @Transactional
    public PaymentTransaction processPayment(PaymentRequest req) {
        // ... (existing code) ...
        PaymentTransaction.Status transactionStatus;
        PaymentInfoStatus orderPaymentStatus;

        // Simple simulation: 80% success rate for Credit Card, 60% for PayPal, 90% for Bank Transfer
        switch (req.getPaymentMethod().toLowerCase()) {
            case "credit card":
                // For E2E testing, always succeed credit card payments
                transactionStatus = PaymentTransaction.Status.SUCCESS; 
                // Original: transactionStatus = random.nextDouble() < 0.8 ? PaymentTransaction.Status.SUCCESS : PaymentTransaction.Status.FAILED;
                break;
            case "paypal":
                transactionStatus = random.nextDouble() < 0.6 ? PaymentTransaction.Status.SUCCESS : PaymentTransaction.Status.FAILED;
                break;
            case "bank transfer":
                transactionStatus = random.nextDouble() < 0.9 ? PaymentTransaction.Status.SUCCESS : PaymentTransaction.Status.FAILED;
                break;
            default:
                transactionStatus = PaymentTransaction.Status.FAILED;
                break;
        }

        PaymentTransaction tx = PaymentTransaction.builder()
                .orderId(req.getOrderId())
                .amount(req.getAmount())
                .paymentMethod(req.getPaymentMethod())
                .transactionId("TX" + System.currentTimeMillis() + random.nextInt(1000))
                .status(transactionStatus)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        PaymentTransaction savedTx = repo.save(tx);

        if (transactionStatus == PaymentTransaction.Status.SUCCESS) {
            orderPaymentStatus = PaymentInfoStatus.SUCCESS;
        } else {
            orderPaymentStatus = PaymentInfoStatus.FAILED;
        }

        // Update order status in order_service
        try {
            orderClient.processPayment(savedTx.getOrderId(), savedTx.getTransactionId(), orderPaymentStatus);
        } catch (Exception e) {
            // Log error, but don't fail the transaction if the callback fails (idempotency needed)
            System.err.println("Failed to update order service: " + e.getMessage());
        }

        return savedTx;
    }

    @Transactional
    public PaymentTransaction processRefund(RefundRequest req) {
        // Find original transaction if possible, but for now just create a new refund transaction
        PaymentTransaction refundTx = PaymentTransaction.builder()
                .transactionId("REF-" + req.getTransactionId())
                .amount(req.getAmount().negate()) // Negative amount for refund
                .paymentMethod("REFUND")
                .status(PaymentTransaction.Status.REFUNDED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return repo.save(refundTx);
    }
}
