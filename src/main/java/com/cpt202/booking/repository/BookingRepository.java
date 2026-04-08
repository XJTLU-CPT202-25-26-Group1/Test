package com.cpt202.booking.repository;

import com.cpt202.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsBySpecialistNameAndBookingDateAndBookingTime(
            String specialistName,
            String bookingDate,
            String bookingTime
    );
}