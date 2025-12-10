package com.example.ticket_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderDetailsDto {
    private Long id;
    private String userId;
    private Long eventId;
    private List<OrderItemDto> items;

    @Data
    public static class OrderItemDto {
        private Long ticketTypeId;
        private int quantity;
    }
}
