package com.cpt202.booking.controller.customer;

import com.cpt202.booking.service.BookingService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer/bookings")
public class CustomerBookingController {

    private final BookingService bookingService;

    public CustomerBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public String bookingList(Authentication authentication, Model model) {
        String email = authentication.getName() + "@demo.local";
        model.addAttribute("bookings", bookingService.getCustomerBookings(email));
        return "customer/bookings";
    }

    @PostMapping("/create")
    public String createBooking(Authentication authentication,
                                @RequestParam Long specialistId,
                                @RequestParam Long slotId,
                                @RequestParam String topic,
                                @RequestParam(required = false) String notes,
                                RedirectAttributes redirectAttributes) {
        try {
            bookingService.createBooking(authentication.getName(), authentication.getName() + "@demo.local", specialistId, slotId, topic, notes);
            redirectAttributes.addFlashAttribute("message", "Booking request submitted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/customer/bookings";
    }

    @PostMapping("/cancel")
    public String cancelBooking(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(id);
            redirectAttributes.addFlashAttribute("message", "Booking cancelled.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/customer/bookings";
    }

    @PostMapping("/reschedule")
    public String rescheduleBooking(@RequestParam Long id, @RequestParam Long newSlotId, RedirectAttributes redirectAttributes) {
        try {
            bookingService.rescheduleBooking(id, newSlotId);
            redirectAttributes.addFlashAttribute("message", "Booking rescheduled and returned to pending review.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/customer/bookings";
    }
}
