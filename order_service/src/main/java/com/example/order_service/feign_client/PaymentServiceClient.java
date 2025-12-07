package com.example.order_service.feign_client;

import com.example.order_service.dto.PaymentRequestDto;
import com.example.order_service.dto.PaymentTransactionDto; // Will create this DTO
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "${payment.service-url}")
public interface PaymentServiceClient {

    @PostMapping("/api/payments")
    PaymentTransactionDto processPayment(@RequestBody PaymentRequestDto request);
}
