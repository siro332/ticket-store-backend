package com.example.ticket_service.service;

import com.example.ticket_service.dto.OrderDetailsDto;
import com.example.ticket_service.dto.OrderPaidEvent;
import com.example.ticket_service.feign_client.OrderServiceClient;
import com.example.ticket_service.model.Ticket;
import com.example.ticket_service.repository.TicketRepository;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private static final String ORDER_PAID_TOPIC = "order.paid";

    private final TicketRepository ticketRepository;
    private final OrderServiceClient orderServiceClient;
    private final Gson gson = new Gson();

    @KafkaListener(topics = ORDER_PAID_TOPIC, groupId = "ticket-group")
    public void listenOrderPaid(String message) {
        System.out.println("Received message from " + ORDER_PAID_TOPIC + ": " + message);
        OrderPaidEvent event = gson.fromJson(message, OrderPaidEvent.class);

        OrderDetailsDto orderDetails = orderServiceClient.getOrderById(event.getOrderId());
        System.out.println("Fetched order details: " + orderDetails);

        List<Ticket> ticketsToSave = new ArrayList<>();
        if (orderDetails.getItems() != null) {
            for (OrderDetailsDto.OrderItemDto item : orderDetails.getItems()) {
                System.out.println("Processing item: " + item);
                for (int i = 0; i < item.getQuantity(); i++) {
                    Ticket ticket = Ticket.builder()
                            .orderId(orderDetails.getId())
                            .eventId(orderDetails.getEventId())
                            .userId(orderDetails.getUserId() != null ? orderDetails.getUserId().toString() : null)
                            .ticketCode(UUID.randomUUID().toString())
                            .attendeeName(event.getUserEmail()) // Use user email as initial attendee name
                            .attendeeEmail(event.getUserEmail())
                            .build();
                    ticketsToSave.add(ticket);
                }
            }
        } else {
            System.out.println("No items found in order " + event.getOrderId());
        }
        ticketRepository.saveAll(ticketsToSave);
        System.out.println("Generated " + ticketsToSave.size() + " tickets for order " + event.getOrderId());
    }
}
