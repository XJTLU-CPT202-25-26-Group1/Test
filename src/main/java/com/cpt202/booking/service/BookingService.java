package com.cpt202.booking.service;

import com.cpt202.booking.model.Booking;
import com.cpt202.booking.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking createBooking(Booking booking) {
        boolean exists = bookingRepository.existsBySpecialistNameAndBookingDateAndBookingTime(
                booking.getSpecialistName(),
                booking.getBookingDate(),
                booking.getBookingTime()
        );

        if (exists) {
            throw new RuntimeException("This specialist is already booked for the selected date and time.");
        }

        if (booking.getStatus() == null || booking.getStatus().isBlank()) {
            booking.setStatus("Pending");
        }

        return bookingRepository.save(booking);
    }

    public Booking cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found."));

        if ("Cancelled".equalsIgnoreCase(booking.getStatus())) {
            throw new RuntimeException("This booking is already cancelled.");
        }

        booking.setStatus("Cancelled");
        return bookingRepository.save(booking);
    }

    public Booking rescheduleBooking(Long id, Booking updatedBooking) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found."));

        if ("Cancelled".equalsIgnoreCase(booking.getStatus())) {
            throw new RuntimeException("Cancelled bookings cannot be rescheduled.");
        }

        boolean exists = bookingRepository.existsBySpecialistNameAndBookingDateAndBookingTime(
                updatedBooking.getSpecialistName(),
                updatedBooking.getBookingDate(),
                updatedBooking.getBookingTime()
        );

        boolean sameSlot =
                booking.getSpecialistName().equals(updatedBooking.getSpecialistName()) &&
                booking.getBookingDate().equals(updatedBooking.getBookingDate()) &&
                booking.getBookingTime().equals(updatedBooking.getBookingTime());

        if (exists && !sameSlot) {
            throw new RuntimeException("The new selected slot is already booked.");
        }

        booking.setSpecialistName(updatedBooking.getSpecialistName());
        booking.setBookingDate(updatedBooking.getBookingDate());
        booking.setBookingTime(updatedBooking.getBookingTime());
        booking.setTopic(updatedBooking.getTopic());
        booking.setStatus("Pending");

        return bookingRepository.save(booking);
    }
}