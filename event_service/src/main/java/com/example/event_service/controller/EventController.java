package com.example.event_service.controller;

import com.example.event_service.model.Discount;
import com.example.event_service.model.Event;
import com.example.event_service.model.Seat;
import com.example.event_service.model.TicketType;
import com.example.event_service.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<Event>> getAll() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
    @PostMapping
    public ResponseEntity<Event> create(@RequestBody Event event) {
        return ResponseEntity.ok(eventService.createEvent(event));
    }

    @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Event> update(@PathVariable Long id, @RequestBody Event event) {
        return ResponseEntity.ok(eventService.updateEvent(id, event));
    }

    @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    // TicketType management endpoints
    @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
    @PostMapping("/{eventId}/ticket-types")
    public ResponseEntity<TicketType> addTicketTypeToEvent(@PathVariable Long eventId, @RequestBody TicketType ticketType) {
        return ResponseEntity.ok(eventService.addTicketTypeToEvent(eventId, ticketType));
    }

    @GetMapping("/{eventId}/ticket-types")
    public ResponseEntity<List<TicketType>> getTicketTypesForEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getTicketTypesForEvent(eventId));
    }

    @GetMapping("/ticket-types/{ticketTypeId}")
    public ResponseEntity<TicketType> getTicketType(@PathVariable Long ticketTypeId) {
        return ResponseEntity.ok(eventService.getTicketTypeById(ticketTypeId));
    }

    @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
    @DeleteMapping("/ticket-types/{ticketTypeId}")
    public ResponseEntity<Void> deleteTicketType(@PathVariable Long ticketTypeId) {
        eventService.deleteTicketType(ticketTypeId);
        return ResponseEntity.noContent().build();
    }

    // Discount management endpoints
    @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
    @PostMapping("/{eventId}/discounts")
    public ResponseEntity<Discount> addDiscountToEvent(@PathVariable Long eventId, @RequestBody Discount discount) {
        return ResponseEntity.ok(eventService.addDiscountToEvent(eventId, discount));
    }

    @GetMapping("/{eventId}/discounts")
    public ResponseEntity<List<Discount>> getDiscountsForEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getDiscountsForEvent(eventId));
    }

    @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
    @DeleteMapping("/discounts/{discountId}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable Long discountId) {
        eventService.deleteDiscount(discountId);
        return ResponseEntity.noContent().build();
    }

    // New endpoint for OrderService to validate discount code
    @GetMapping("/{eventId}/discounts/validate")
    public ResponseEntity<Discount> validateDiscountCode(@PathVariable Long eventId, @RequestParam String code) {
        return eventService.validateDiscountCode(eventId, code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // New endpoint to increment discount usage count
    @PostMapping("/discounts/{discountId}/increment-usage")
    public ResponseEntity<Void> incrementDiscountUsage(@PathVariable Long discountId) {
        eventService.incrementDiscountUsedCount(discountId);
        return ResponseEntity.noContent().build();
    }


    // Seat management endpoints
    @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
    @PostMapping("/{eventId}/seats")
    public ResponseEntity<List<Seat>> addSeatsToEvent(@PathVariable Long eventId, @RequestBody List<Seat> seats) {
        return ResponseEntity.ok(eventService.addSeatsToEvent(eventId, seats));
    }

    @GetMapping("/{eventId}/seats")
    public ResponseEntity<List<Seat>> getSeatsForEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getSeatsForEvent(eventId));
    }

    @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
    @PutMapping("/seats/{seatId}/availability")
    public ResponseEntity<Void> updateSeatAvailability(@PathVariable Long seatId, @RequestParam Boolean isAvailable) {
        eventService.updateSeatAvailability(seatId, isAvailable);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
    @PutMapping("/seats/{seatId}/lock")
    public ResponseEntity<Void> updateSeatLockStatus(@PathVariable Long seatId, @RequestParam Boolean locked) {
        eventService.updateSeatLockStatus(seatId, locked);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Event>> searchEvents(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String location,
            Pageable pageable) {
        Page<Event> events = eventService.searchEvents(keyword, category, startTime, endTime, minPrice, maxPrice, location, pageable);
        return ResponseEntity.ok(events);
    }
}
