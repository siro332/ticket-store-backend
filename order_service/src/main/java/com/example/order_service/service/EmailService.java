package com.example.order_service.service;

import com.example.order_service.util.QrCodeGenerator;
import com.google.zxing.WriterException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOrderConfirmationEmail(String recipientEmail, String orderDetails) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setTo(recipientEmail);
            helper.setSubject("Your Order Confirmation");
            helper.setText("Dear Customer,\n\n" +
                           "Thank you for your order! Here are your order details:\n\n" +
                           orderDetails +
                           "\n\nWe look forward to seeing you at the event!\n\n" +
                           "Best regards,\nYour Ticket Store Team", true); // true for HTML content

            mailSender.send(message);
            log.info("Order confirmation email sent to: {}", recipientEmail);
        } catch (MessagingException | MailException e) { // Catch MailException
            log.warn("Failed to send order confirmation email to {}: {}", recipientEmail, e.getMessage());
        }
    }

    public void sendTicketEmail(String recipientEmail, String ticketDetails, String ticketCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name()); // true for multipart message

            helper.setTo(recipientEmail);
            helper.setSubject("Your Event Ticket - " + ticketCode);

            String htmlContent = "Dear Attendee,<br/><br/>" +
                                 "Here is your ticket for the event:<br/><br/>" +
                                 ticketDetails + "<br/><br/>" +
                                 "Your Ticket Code: <b>" + ticketCode + "</b><br/><br/>" +
                                 "Please present this code (or the attached QR code) at the entrance.<br/><br/>" +
                                 "Thank you!<br/><br/>" +
                                 "Best regards,<br/>Your Ticket Store Team";

            // Generate QR code
            byte[] qrCodeImageBytes = QrCodeGenerator.generateQrCodeImage(ticketCode);
            ByteArrayResource qrCodeResource = new ByteArrayResource(qrCodeImageBytes);

            htmlContent += "<br/><br/><img src='cid:qrCodeImage'/><br/>"; // Embed QR code
            helper.addInline("qrCodeImage", qrCodeResource, "image/png");


            helper.setText(htmlContent, true); // true for HTML content

            mailSender.send(message);
            log.info("Ticket email sent to: {}", recipientEmail);
        } catch (MessagingException | MailException | WriterException | IOException e) { // Catch MailException
            log.warn("Failed to send ticket email to {}: {}", recipientEmail, e.getMessage());
        }
    }

    // Other notification methods can be added here (e.g., event reminders, schedule changes)
}
