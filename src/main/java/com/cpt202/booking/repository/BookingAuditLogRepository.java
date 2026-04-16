package com.cpt202.booking.repository;

import com.cpt202.booking.model.BookingAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingAuditLogRepository extends JpaRepository<BookingAuditLog, Long> {

    List<BookingAuditLog> findByBookingIdOrderByOperatedAtDesc(Long bookingId);

    List<BookingAuditLog> findAllByOrderByOperatedAtDesc();
}
