package com.cpt202.booking.controller;

import com.cpt202.booking.model.Booking;
import com.cpt202.booking.service.BookingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {
        return bookingService.createBooking(booking);
    }

    @PutMapping("/{id}/cancel")
    public Booking cancelBooking(@PathVariable Long id) {
        return bookingService.cancelBooking(id);
    }

    @PutMapping("/{id}/reschedule")
    public Booking rescheduleBooking(
            @PathVariable Long id,
            @RequestBody Booking updatedBooking) {
        return bookingService.rescheduleBooking(id, updatedBooking);
    }
}