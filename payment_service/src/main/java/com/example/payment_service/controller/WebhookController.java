package com.example.payment_service.controller;

import com.example.payment_service.dto.WebhookPayload;
import com.example.payment_service.service.PaymentService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final PaymentService service;

    @PostMapping
    public ResponseEntity<Void> handle(@RequestBody WebhookPayload payload) {
        service.handleCallback(payload.getTransactionId(), payload.isSuccess());
        return ResponseEntity.ok().build();
    }
}

