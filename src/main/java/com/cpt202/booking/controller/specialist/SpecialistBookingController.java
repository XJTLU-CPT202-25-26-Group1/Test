package com.cpt202.booking.controller.specialist;

import com.cpt202.booking.service.BookingService;
import com.cpt202.booking.service.SpecialistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/specialist/bookings")
public class SpecialistBookingController {

    private final BookingService bookingService;
    private final SpecialistService specialistService;

    public SpecialistBookingController(BookingService bookingService, SpecialistService specialistService) {
        this.bookingService = bookingService;
        this.specialistService = specialistService;
    }

    @GetMapping
    public String bookingPage(Model model) {
        Long specialistId = specialistService.getAllSpecialists().get(0).getId();
        model.addAttribute("bookings", bookingService.getSpecialistBookings(specialistId));
        return "specialist/bookings";
    }

    @PostMapping("/complete")
    public String complete(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            bookingService.completeBooking(id);
            redirectAttributes.addFlashAttribute("message", "Booking marked as completed.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/specialist/bookings";
    }
}
