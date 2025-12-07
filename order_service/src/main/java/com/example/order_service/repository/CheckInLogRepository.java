package com.example.order_service.repository;

import com.example.order_service.model.CheckInLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckInLogRepository extends JpaRepository<CheckInLog, Long> {
    List<CheckInLog> findByEventId(Long eventId);
    List<CheckInLog> findByTicketId(Long ticketId);
}
