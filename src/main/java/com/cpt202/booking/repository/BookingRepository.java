package com.cpt202.booking.repository;

import com.cpt202.booking.enums.BookingStatus;
import com.cpt202.booking.model.Booking;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByCustomerEmailOrderByCreatedAtDesc(String customerEmail);

    List<Booking> findByStatusOrderByCreatedAtAsc(BookingStatus status);

    List<Booking> findBySpecialistIdOrderByCreatedAtDesc(Long specialistId);

    boolean existsBySpecialistId(Long specialistId);

    List<Booking> findByCustomerEmailAndStatusOrderByCreatedAtDesc(String customerEmail, BookingStatus status);

    Optional<Booking> findByIdAndCustomerEmail(Long id, String customerEmail);

    List<Booking> findByCustomerEmailAndTopicContainingIgnoreCaseOrderByCreatedAtDesc(String customerEmail, String topic);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select booking from Booking booking where booking.id = :id")
    Optional<Booking> findByIdForUpdate(@Param("id") Long id);
}
