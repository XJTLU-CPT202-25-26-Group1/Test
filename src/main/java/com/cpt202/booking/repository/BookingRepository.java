package com.cpt202.booking.repository;

import com.cpt202.booking.enums.BookingStatus;
import com.cpt202.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByCustomerEmailOrderByCreatedAtDesc(String customerEmail);

    List<Booking> findByStatusOrderByCreatedAtAsc(BookingStatus status);

    List<Booking> findBySpecialistIdOrderByCreatedAtDesc(Long specialistId);

    List<Booking> findByCustomerEmailAndStatusOrderByCreatedAtDesc(String customerEmail, BookingStatus status);

    Optional<Booking> findByIdAndCustomerEmail(Long id, String customerEmail);

    List<Booking> findByCustomerEmailAndTopicContainingIgnoreCaseOrderByCreatedAtDesc(String customerEmail, String topic);
}
