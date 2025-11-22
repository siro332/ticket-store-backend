package com.example.payment_service.feign_client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "orderClient", url = "${order.service-url}")
public interface OrderClient {

    @PutMapping("/{id}/status")
    void updateOrderStatus(@PathVariable("id") Long orderId,
                           @RequestParam("status") String status);
}
