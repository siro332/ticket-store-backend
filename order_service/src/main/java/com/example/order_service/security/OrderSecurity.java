package com.example.order_service.security;

import com.example.order_service.model.Order;
import com.example.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("orderSecurity")
@RequiredArgsConstructor
public class OrderSecurity {

    private final OrderService orderService;

    public Order getOrder(Long orderId) {
        return orderService.getOrder(orderId);
    }
}
