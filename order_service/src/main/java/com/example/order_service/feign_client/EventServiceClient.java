package com.example.order_service.feign_client;

import com.example.order_service.dto.DiscountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@FeignClient(name = "event-service", url = "${event.service-url}")
public interface EventServiceClient {

    @GetMapping("/api/events/{eventId}/discounts/validate")
    Optional<DiscountDto> validateDiscountCode(@PathVariable("eventId") Long eventId, @RequestParam("code") String code);
}
