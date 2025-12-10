package com.example.ticket_service.controller;

import com.example.ticket_service.dto.PaymentResponse;
import com.example.ticket_service.dto.PostTicketRequest;
import com.example.ticket_service.model.MarketplaceListing;
import com.example.ticket_service.service.MarketplaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/marketplace")
@RequiredArgsConstructor
public class MarketplaceController {

    private final MarketplaceService marketplaceService;

    @GetMapping
    public ResponseEntity<List<MarketplaceListing>> getActiveListings() {
        return ResponseEntity.ok(marketplaceService.getActiveListings());
    }

    @PostMapping
    public ResponseEntity<MarketplaceListing> postTicket(@RequestBody PostTicketRequest request) {
        return ResponseEntity.ok(marketplaceService.postTicket(request));
    }

    @PostMapping("/{listingId}/buy")
    public ResponseEntity<PaymentResponse> buyTicket(@PathVariable UUID listingId, @RequestBody String buyerId) {
        return ResponseEntity.ok(marketplaceService.buyTicket(listingId, buyerId));
    }
}
