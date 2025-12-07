package com.example.order_service.service;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

    public void sendOrderConfirmationEmail(String recipientEmail, String orderDetails) {
        log.info("Sending order confirmation email to: {}", recipientEmail);
        log.info("Order Details: {}", orderDetails);
        // In a real application, this would integrate with an actual email sending service (e.g., SendGrid, JavaMailSender)
        // For now, we'll just log the email content.
        System.out.println("--- EMAIL SENT ---");
        System.out.println("To: " + recipientEmail);
        System.out.println("Subject: Your Order Confirmation");
        System.out.println("Body: " + orderDetails);
        System.out.println("------------------");
    }

    // Other notification methods can be added here (e.g., event reminders, schedule changes)
}
