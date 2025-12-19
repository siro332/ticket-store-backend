package com.example.event_service.service;

import com.example.event_service.dto.EventTransferPolicyDto;
import com.example.event_service.dto.ReservationDto;
import com.example.event_service.feign_client.OrderServiceClient;
import com.example.event_service.model.Discount;
import com.example.event_service.model.Event;
import com.example.event_service.model.Seat;
import com.example.event_service.model.TicketType;
import com.example.event_service.repository.DiscountRepository;
import com.example.event_service.repository.EventRepository;
import com.example.event_service.repository.SeatRepository;
import com.example.event_service.repository.TicketTypeRepository;
import com.example.event_service.specification.EventSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private static final Logger log = LoggerFactory.getLogger(EventService.class); // Logger instance

    private final EventRepository eventRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final DiscountRepository discountRepository;
    private final SeatRepository seatRepository;
    private final OrderServiceClient orderServiceClient; // Inject Feign Client

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    public Event createEvent(Event event) {
        // Ensure default values are set if not provided
        if (event.getAllowTicketTransfer() == null) event.setAllowTicketTransfer(false);
        if (event.getAllowAttendeeNameChange() == null) event.setAllowAttendeeNameChange(false);
        return eventRepository.save(event);
    }

    @Transactional
    public Event updateEvent(Long id, Event updated) {
        Event existing = getById(id);
        log.debug("Updating event ID {}. Existing: {}", id, existing);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setCategory(updated.getCategory());
        existing.setVenue(updated.getVenue()); // Assuming venue is handled as an entity or ID
        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());
        existing.setCoverImage(updated.getCoverImage());
        // Status update is handled via specific workflows (submit/approve/cancel)
        existing.setAllowTicketTransfer(updated.getAllowTicketTransfer()); // Update new field
        existing.setAllowAttendeeNameChange(updated.getAllowAttendeeNameChange()); // Update new field
        existing.setRefundEnabled(updated.getRefundEnabled());
        existing.setRefundDeadlineHours(updated.getRefundDeadlineHours());
        existing.setRefundFeePercent(updated.getRefundFeePercent());
        Event savedEvent = eventRepository.save(existing);
        log.debug("Event ID {} updated to: {}", id, savedEvent);
        return savedEvent;
    }

    @Transactional
    public Event cancelEvent(Long id) {
        Event event = getById(id);
        event.setStatus(Event.Status.CANCELLED);
        return eventRepository.save(event);
    }

    @Transactional
    public Event submitForApproval(Long id) {
        Event event = getById(id);
        if (event.getStatus() != Event.Status.DRAFT) {
            throw new RuntimeException("Only DRAFT events can be submitted for approval.");
        }
        event.setStatus(Event.Status.PENDING_APPROVAL);
        return eventRepository.save(event);
    }

    @Transactional
    public Event approveEvent(Long id) {
        Event event = getById(id);
        if (event.getStatus() != Event.Status.PENDING_APPROVAL) {
            throw new RuntimeException("Only PENDING_APPROVAL events can be approved.");
        }
        event.setStatus(Event.Status.PUBLISHED);
        return eventRepository.save(event);
    }
    
    @Transactional
    public Event updateStatus(Long id, Event.Status status) {
        Event event = getById(id);
        event.setStatus(status);
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    // Methods for TicketType management
    public TicketType addTicketTypeToEvent(Long eventId, TicketType ticketType) {
        Event event = getById(eventId);
        ticketType.setEvent(event);
        return ticketTypeRepository.save(ticketType);
    }

    public List<TicketType> getTicketTypesForEvent(Long eventId) {
        return ticketTypeRepository.findByEventId(eventId);
    }

    public TicketType getTicketTypeById(Long id) {
        return ticketTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket type not found"));
    }

    public void deleteTicketType(Long ticketTypeId) {
        ticketTypeRepository.deleteById(ticketTypeId);
    }

    // Methods for Discount management
    public Discount addDiscountToEvent(Long eventId, Discount discount) {
        Event event = getById(eventId);
        discount.setEvent(event);
        return discountRepository.save(discount);
    }

    public List<Discount> getDiscountsForEvent(Long eventId) {
        return discountRepository.findByEventId(eventId);
    }

    public void deleteDiscount(Long discountId) {
        discountRepository.deleteById(discountId);
    }

    // New method to validate discount code for Feign client
    public Optional<Discount> validateDiscountCode(Long eventId, String code) {
        return discountRepository.findByEventIdAndCode(eventId, code);
    }

    @Transactional
    public void incrementDiscountUsedCount(Long discountId) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new RuntimeException("Discount not found with id: " + discountId));
        if (discount.getUsedCount() == null) {
            discount.setUsedCount(0);
        }
        discount.setUsedCount(discount.getUsedCount() + 1);
        discountRepository.save(discount);
    }

    // Methods for Seat management
    public List<Seat> addSeatsToEvent(Long eventId, List<Seat> seats) {
        Event event = getById(eventId);
        seats.forEach(seat -> seat.setEvent(event));
        return seatRepository.saveAll(seats);
    }

    // Modified getSeatsForEvent to include real-time availability from reservations
    public List<Seat> getSeatsForEvent(Long eventId) {
        List<Seat> seats = seatRepository.findByEventId(eventId);
        List<ReservationDto> activeReservations = orderServiceClient.getActiveReservationsForEvent(eventId);

        // Collect seatIds that are currently reserved
        Set<Long> reservedSeatIds = activeReservations.stream()
                .filter(r -> r.getSeatId() != null)
                .map(ReservationDto::getSeatId)
                .collect(Collectors.toSet());

        // Update the availability status of seats based on reservations
        return seats.stream().map(seat -> {
            // If a seat is explicitly locked by an organizer, it remains locked
            // Otherwise, check if it's reserved
            if (!seat.getLocked() && reservedSeatIds.contains(seat.getId())) {
                seat.setIsAvailable(false); // Mark as unavailable due to reservation
            }
            return seat;
        }).collect(Collectors.toList());
    }

    public void updateSeatAvailability(Long seatId, Boolean isAvailable) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Seat not found"));
        seat.setIsAvailable(isAvailable);
        seatRepository.save(seat);
    }

    public void updateSeatLockStatus(Long seatId, Boolean locked) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Seat not found"));
        seat.setLocked(locked);
        seatRepository.save(seat);
    }

    // New method to get event transfer policies
    public EventTransferPolicyDto getEventTransferPolicy(Long eventId) {
        Event event = getById(eventId);
        return EventTransferPolicyDto.builder()
                .allowTicketTransfer(event.getAllowTicketTransfer())
                .allowAttendeeNameChange(event.getAllowAttendeeNameChange())
                .build();
    }

    public Page<Event> searchEvents(String keyword, String category, LocalDateTime startTime, LocalDateTime endTime, BigDecimal minPrice, BigDecimal maxPrice, String location, Pageable pageable) {
        Specification<Event> spec = EventSpecification.withFilters(keyword, category, startTime, endTime, minPrice, maxPrice, location);
        return eventRepository.findAll(spec, pageable);
    }
}
