package com.cpt202.booking.controller;

import com.cpt202.booking.service.BookingService;
import com.cpt202.booking.service.SpecialistService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private final SpecialistService specialistService;
    private final BookingService bookingService;

    public PageController(SpecialistService specialistService, BookingService bookingService) {
        this.specialistService = specialistService;
        this.bookingService = bookingService;
    }

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        model.addAttribute("specialists", specialistService.getAllSpecialists());
        model.addAttribute("pendingCount", bookingService.getPendingBookings().size());
        model.addAttribute("currentUser", authentication != null ? authentication.getName() : "Guest");
        return "home";
    }
}
