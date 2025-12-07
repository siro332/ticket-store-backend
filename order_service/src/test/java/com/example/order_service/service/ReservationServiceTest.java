package com.example.order_service.service;

import com.example.order_service.dto.ReservationRequest;
import com.example.order_service.dto.TicketTypeDto;
import com.example.order_service.feign_client.EventServiceClient;
import com.example.order_service.model.Reservation;
import com.example.order_service.repository.ReservationRepository;
import com.example.order_service.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private EventServiceClient eventServiceClient;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private ReservationService reservationService;

    private ReservationRequest request;
    private TicketTypeDto ticketType;

    @BeforeEach
    void setUp() {
        request = ReservationRequest.builder()
                .userId(1L)
                .eventId(10L)
                .ticketTypeId(100L)
                .quantity(2)
                .build();

        ticketType = TicketTypeDto.builder()
                .id(100L)
                .purchaseLimit(5)
                .build();
    }

    @Test
    void reserve_Success_UnderLimit() {
        when(eventServiceClient.getTicketTypeById(100L)).thenReturn(ticketType);
        // Mock counts: 2 purchased + 0 reserved + 2 requested = 4 <= 5
        when(ticketRepository.countByOrderItem_Order_UserIdAndOrderItem_TicketTypeId(1L, 100L)).thenReturn(2L);
        when(reservationRepository.findByUserIdAndStatusAndExpireAtAfter(eq(1L), eq(Reservation.ReservationStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArguments()[0]);

        Reservation res = reservationService.reserve(request);
        
        assertNotNull(res);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void reserve_Fail_OverLimit() {
        ticketType.setPurchaseLimit(2); // Set limit low
        when(eventServiceClient.getTicketTypeById(100L)).thenReturn(ticketType);
        // Mock counts: 1 purchased + 0 reserved + 2 requested = 3 > 2
        when(ticketRepository.countByOrderItem_Order_UserIdAndOrderItem_TicketTypeId(1L, 100L)).thenReturn(1L);
        when(reservationRepository.findByUserIdAndStatusAndExpireAtAfter(eq(1L), eq(Reservation.ReservationStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> reservationService.reserve(request));
        assert(exception.getMessage().contains("Purchase limit exceeded"));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }
}
