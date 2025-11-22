package com.example.payment_service.service;

import com.example.payment_service.dto.PaymentRequest;
import com.example.payment_service.feign_client.OrderClient;
import com.example.payment_service.model.PaymentTransaction;
import com.example.payment_service.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentTransactionRepository repo;
    private final OrderClient orderClient;

    public PaymentTransaction createPayment(PaymentRequest req) {

        PaymentTransaction tx = PaymentTransaction.builder()
                .orderId(req.getOrderId())
                .amount(req.getAmount())
                .gateway(req.getGateway())
                .transactionId("TX" + System.currentTimeMillis())  // mock txid
                .status(PaymentTransaction.Status.PENDING)
                .build();

        return repo.save(tx);
    }

    public void handleCallback(String txId, boolean success) {
        PaymentTransaction tx = repo.findByTransactionId(txId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        tx.setStatus(success ? PaymentTransaction.Status.SUCCESS : PaymentTransaction.Status.FAILED);
        repo.save(tx);

        orderClient.updateOrderStatus(tx.getOrderId(), success ? "PAID" : "FAILED");
    }
}
