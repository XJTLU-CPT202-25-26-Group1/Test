package com.cpt202.booking.controller;

import com.cpt202.booking.service.BookingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<?> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @PostMapping
    public Object createBooking(@RequestBody Map<String, String> payload) {
        return bookingService.createBooking(
                payload.getOrDefault("customerName", "customer"),
                payload.getOrDefault("customerEmail", "customer@demo.local"),
                Long.parseLong(payload.get("specialistId")),
                Long.parseLong(payload.get("slotId")),
                payload.getOrDefault("topic", "Consultation"),
                payload.getOrDefault("notes", "")
        );
    }

    @PutMapping("/{id}/cancel")
    public Object cancelBooking(@PathVariable Long id) {
        return bookingService.cancelBooking(id);
    }

    @PutMapping("/{id}/reschedule")
    public Object rescheduleBooking(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        return bookingService.rescheduleBooking(id, Long.parseLong(payload.get("newSlotId")));
    }
}
